package ru.foobarbaz.neuralnetwork.perceptron.logic;

import ru.foobarbaz.neuralnetwork.function.SigmoidFunction;
import java.util.function.Function;

public class PerceptronImpl implements Perceptron {
    private static final double STUDYING_POWER = 0.1;
    protected double[][][] weights;
    protected double[][] neurons;
    private double[][] errors;
    private Function<Double, Double> activateFunction = new SigmoidFunction();

    public PerceptronImpl(int... neuronsOnLayers){
        if (neuronsOnLayers.length < 2) {
            throw new IllegalArgumentException("Requires two layers, at least");
        }

        initNeurons(neuronsOnLayers);
        initWeights(neuronsOnLayers);
        initErrors(neuronsOnLayers);
    }

    @Override
    public void study(double[] input, double[] expectedOutput) {
        if (expectedOutput.length != neurons[neurons.length - 1].length) {
            throw new IllegalArgumentException("The number of output values must be equal to the number of neurons in the last layer");
        }
        double[] actualOutput = process(input);

        double[] errors = new double[actualOutput.length];
        int outputLayerNum=neurons.length-1;
        for (int i = 0; i < errors.length; i++) {
            errors[i] =(expectedOutput[i]- actualOutput[i])*getDerValues(outputLayerNum,i);
            this.errors[outputLayerNum][i]=errors[i];
            double[] inputLinks=weights[outputLayerNum-1][i];
            for(int j=0;j<inputLinks.length;j++){
                double weightDelta=errors[i]*neurons[outputLayerNum-1][j]* STUDYING_POWER;
                weights[outputLayerNum-1][i][j]=weights[outputLayerNum-1][i][j]+weightDelta;
            }
        }
        int countOfLayers=neurons.length-1;
        for(int k=countOfLayers-1;k>0;k--){
            double[] middleLayer=neurons[k];
            for(int i=0;i<middleLayer.length;i++){
                double errorSum=0;

                for(int j=0;j<neurons[k+1].length;j++){
                    errorSum += weights[k][j][i]*this.errors[k+1][j];
                }
                double error=errorSum*getDerValues(k,i);
                this.errors[k][i]=error;
                for(int j=0; j<neurons[k-1].length;j++){
                    double weightDelta=error*neurons[k-1][j] * STUDYING_POWER;
                    weights[k-1][i][j]=weights[k-1][i][j]+weightDelta;
                }
            }
        }

    }

    @Override
    public int getLayers() {
        return neurons.length;
    }

    @Override
    public double[] getNeurons(int layer) {
        return neurons[layer].clone();
    }

    private double getDerValues(int layer, int neuron){
        double sum=0;
        double[] inputLinks=weights[layer-1][neuron];
        for(int i=0;i<inputLinks.length;i++){
            sum+=inputLinks[i]*neurons[layer-1][i];
        }
        return derValue(sum);
    }

    private double derValue(double value){
        double func = activateFunction.apply(value);
        return func*(1-func);
    }

    private void initErrors(int[] neuronsOnLayers){
        errors = new double[neuronsOnLayers.length][];
        for (int i = 0; i < neuronsOnLayers.length; i++) {
            errors[i] = new double[neuronsOnLayers[i]];
        }
    }

    @Override
    public double[] process(double[] input) {
        setInput(input);
        calculate();
        return getOutput();
    }

    /**
     * Creates array of layers with neurons.
     * @param neuronsOnLayers array with numbers of neurons in layers
     */
    protected void initNeurons(int[] neuronsOnLayers){
        neurons = new double[neuronsOnLayers.length][];
        for (int i = 0; i < neuronsOnLayers.length; i++) {
            neurons[i] = new double[neuronsOnLayers[i]];
        }
    }

    /**
     * Creates weight for each pair of neurons in adjacent layers.
     * Weights are stored in arrays in next format:  weights[layer][next_layer_neuron][this_layer_neuron].
     * Fills all of weights by random values.
     * @param neuronsOnLayers array with numbers of neurons in layers
     */
    private void initWeights(int[] neuronsOnLayers){
        weights = new double[neuronsOnLayers.length - 1][][];

        for (int i = 0; i < weights.length; i++) {
            weights[i] = new double[neuronsOnLayers[i+1]][];

            for (int j = 0; j < weights[i].length; j++) {

                weights[i][j] = new double[neuronsOnLayers[i]];
                for (int k = 0; k < weights[i][j].length; k++) {
                    weights[i][j][k] = Math.random() - 0.5;
                }
            }
        }
    }

    /**
     * Sets first (input) layer of network.
     * @param input array of input values. Values must must be between 0 and 1
     */
    private void setInput(double[] input){
        if (input.length != neurons[0].length) {
            throw new IllegalArgumentException("The number of input values must be equal to the number of neurons in the first layer");
        }

        for (double value : input) {
            if (value < 0 || value > 1) { //Input must be normalized
                throw new IllegalArgumentException("Input values must be between 0 and 1");
            }
        }
        neurons[0] = input.clone();
    }

    /**
     * Returns result of neural network's processing
     * @return copy of last layer of network
     */
    private double[] getOutput(){
        return neurons[neurons.length - 1].clone();
    }

    /**
     * Calculates all of neurons in the network
     */
    private void calculate(){
        for (int i = 1; i < neurons.length; i++) {
            for (int j = 0; j < neurons[i].length; j++) {
                calculateNeuron(i, j);
            }
        }
    }

    /**
     * Calculates one neuron. Value = sum()
     * @param layer neuron's layer. Can't be zero, because neurons of first layer would be filled by input values.
     * @param neuron neuron's position in this layer
     */
    private void calculateNeuron(int layer, int neuron){
        double sum = 0;

        for (int i = 0; i < neurons[layer-1].length; i++) {
            sum += neurons[layer-1][i] * weights[layer-1][neuron][i];
        }

        neurons[layer][neuron] = activateFunction.apply(sum);
    }
}
