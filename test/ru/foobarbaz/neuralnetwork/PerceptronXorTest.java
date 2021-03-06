package ru.foobarbaz.neuralnetwork;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.foobarbaz.neuralnetwork.perceptron.logic.Perceptron;
import ru.foobarbaz.neuralnetwork.perceptron.logic.PerceptronImpl;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class PerceptronXorTest {

    private static Perceptron perceptron;
    private static final double MAX_ERROR = 0.1;

    private double[] input;
    private double expectedOutput;

    @BeforeClass
    public static void setUp() throws Exception {
        perceptron = new PerceptronImpl(2, 3, 1);
        for (int i = 0; i < 100000; i++) {
            for (Object[] params : data()) {
                double[] input = (double[]) params[0];
                double expectedOutput = (double) params[1];
                perceptron.study(input, new double[]{expectedOutput});
            }
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[]{new double[]{1,1}, 0d},
                new Object[]{new double[]{0,1}, 1d},
                new Object[]{new double[]{1,0}, 1d},
                new Object[]{new double[]{0,0}, 0d}
        );
    }

    public PerceptronXorTest(double[] input, double expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    @Test
    public void process() throws Exception {
        System.out.println("Input: " + Arrays.toString(input));
        double actualOutput = perceptron.process(input)[0];
        double error = Math.abs(actualOutput - expectedOutput);

        System.out.println("Expected: " + expectedOutput);
        System.out.println("Actual: " + actualOutput);
        System.out.println("Error: " + error);
        Assert.assertTrue(error < MAX_ERROR);
    }
}