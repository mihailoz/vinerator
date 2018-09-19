package rs.kimimaro;

import rs.kimimaro.MLP.MLPHappinessClassifier;
/**
 * Created by mihailozdravkovic on 9/15/18.
 */
public class Application {

    public static void main(String[] args) {
        try {
            new MLPHappinessClassifier();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
