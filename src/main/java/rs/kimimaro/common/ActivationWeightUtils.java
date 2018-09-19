package rs.kimimaro.common;

import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivationWeightUtils {

    public static List<Activation[]> getAllActivations(int num) {
        Activation[] allActivations = Activation.values();
        List<Activation[]> actList = new ArrayList<>();
        recursiveActivation(Arrays.asList(allActivations), 0, num, new Activation[num], actList);
        return actList;
    }

    public static void recursiveActivation(List<Activation> l, int i, int k, Activation[] set, List<Activation[]> actList) {
        for(Activation a : l) {
            set[i] = a;
            if(i == k - 1) {
                actList.add(set.clone());
            } else {
                recursiveActivation(l, i+1, k, set, actList);
            }
        }
    }

    public static List<WeightInit[]> getAllWeightInits(int num) {
        WeightInit[] allWeightInits = WeightInit.values();
        List<WeightInit[]> wList = new ArrayList<>();
        List<WeightInit> allWI = Arrays.asList(allWeightInits);
        recursiveWeight(allWI, 0, num, new WeightInit[num], wList);
        return wList;
    }

    public static void recursiveWeight(List<WeightInit> l, int i, int k, WeightInit[] set, List<WeightInit[]> wList) {
        for(WeightInit w : l) {
            set[i] = w;
            if(i == k - 1) {
                wList.add(set.clone());
            } else {
                recursiveWeight(l, i+1, k, set, wList);
            }
        }
    }
}
