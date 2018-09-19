package rs.kimimaro.MLP;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import rs.kimimaro.common.ActivationWeightUtils;
import rs.kimimaro.datavec.Data;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MLPTester {

    public MLPTester(Data data) throws Exception {
        File file = new File("src/main/resources/data_transformed/part-00000");

        int waitTime = 0;
        while(!file.exists()) {
            wait(1000);
            waitTime += 1000;
            if(waitTime > 20000)
                throw new Exception("Waited 20s for file... exiting!");
        }

        RecordReader recordReader = new CSVRecordReader(0, ',');

        recordReader.initialize(new FileSplit(file));

        int labelIndex = data.getLabelIndex();
        int numClasses = data.getNumberOfClasses();
        int batchSize = 1000;

        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelIndex, numClasses);
        DataSet allData = iterator.next();
        allData.shuffle();
        SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.80);

        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        DataNormalization normalizer = new NormalizerMinMaxScaler();
        normalizer.fit(trainingData);           //Collect the statistics (mean/stdev) from the training data. This does not modify the input data
        normalizer.transform(trainingData);     //Apply normalization to the training data
        normalizer.transform(testData);

        List<Activation[]> activations = ActivationWeightUtils.getAllActivations(1);
        List<WeightInit[]> weightInits = ActivationWeightUtils.getAllWeightInits(3);
        Evaluation maxEval = null;
        Activation[] maxAct = null;
        WeightInit[] maxW = null;

        int index = 1;
        for(WeightInit[] w : weightInits) {

            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                    .seed(123)
                    .updater(new Nesterovs(0.05, 0.9))
                    .list()
                    .layer(0, new DenseLayer.Builder().nIn(numClasses - 1).nOut(200)
                            .weightInit(WeightInit.SIGMOID_UNIFORM)
                            .activation(Activation.SELU)
                            .build())
                    .layer(1, new DenseLayer.Builder().nIn(200).nOut(100)
                            .weightInit(WeightInit.XAVIER)
                            .activation(Activation.HARDTANH)
                            .build())
                    .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                            .weightInit(WeightInit.XAVIER)
                            .activation(Activation.SOFTMAX)
                            .nIn(100).nOut(numClasses).build())
                    .pretrain(false).backprop(true).build();


            MultiLayerNetwork model = new MultiLayerNetwork(conf);
            try {
                model.init();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Skipping evaluation " + index + "/" + weightInits.size());
                index++;
                continue;
            }

            for (int n = 0; n < 25; n++) {
                trainingData.shuffle();
                model.fit(trainingData);
            }

            Evaluation eval = new Evaluation(2);
            INDArray output = model.output(testData.getFeatures());
            eval.eval(testData.getLabels(), output);

            if(maxEval == null || eval.accuracy() > maxEval.accuracy()) {
                maxEval = eval;
                maxW = w;

                System.out.println("New max" + index + "/" + weightInits.size() + "\n" + maxEval.stats());
                Arrays.stream(maxW).forEach(System.out::println);
            }

            System.out.println("Evaluated " + index + "/" + weightInits.size());
            index++;
        }
        System.out.println(maxEval.stats());
        Arrays.stream(maxW).forEach(System.out::println);
    }
}
