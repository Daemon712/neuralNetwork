package ru.foobarbaz.neuralnetwork;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.foobarbaz.neuralnetwork.impl.Teacher;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class PerceptronTeacherTest {
    private static final double MAX_ERROR = 0.1;
    private static NeuralNetwork network;
    private double[] input;
    private double[] expectedOutput;

    @BeforeClass
    public static void setUp() throws Exception {
        Teacher teacher = new Teacher();
        teacher.study(10000);
        network = teacher.getNeuralNetwork();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[]{new double[]{
                        0, 0, 1,
                        0, 1, 1,
                        0, 0, 1,
                        0, 0, 1,
                        0, 0, 1}, new double[]{0, 1, 0, 0, 0, 0, 0, 0, 0, 0}},
                new Object[]{new double[]{
                        1, 1, 1,
                        0, 0, 1,
                        0, 1, 0,
                        1, 0, 0,
                        1, 1, 1}, new double[]{0, 0, 1, 0, 0, 0, 0, 0, 0, 0}},
                new Object[]{new double[]{
                        1, 1, 1,
                        0, 0, 1,
                        1, 1, 1,
                        0, 0, 1,
                        1, 1, 1}, new double[]{0, 0, 0, 1, 0, 0, 0, 0, 0, 0}},
                new Object[]{new double[]{
                        1, 0, 1,
                        1, 0, 1,
                        1, 1, 1,
                        0, 0, 1,
                        0, 0, 1}, new double[]{0, 0, 0, 0, 1, 0, 0, 0, 0, 0}},
                new Object[]{new double[]{
                        1, 1, 1,
                        1, 0, 0,
                        1, 1, 1,
                        0, 0, 1,
                        1, 1, 1}, new double[]{0, 0, 0, 0, 0, 1, 0, 0, 0, 0}},
                new Object[]{new double[]{
                        1, 1, 1,
                        1, 0, 0,
                        1, 1, 1,
                        1, 0, 1,
                        1, 1, 1}, new double[]{0, 0, 0, 0, 0, 0, 1, 0, 0, 0}},
                new Object[]{new double[]{
                        1, 1, 1,
                        0, 0, 1,
                        0, 0, 1,
                        0, 1, 0,
                        0, 1, 0}, new double[]{0, 0, 0, 0, 0, 0, 0, 1, 0, 0}},
                new Object[]{new double[]{
                        1, 1, 1,
                        1, 0, 1,
                        1, 1, 1,
                        1, 0, 1,
                        1, 1, 1}, new double[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 0}},
                new Object[]{new double[]{
                        1, 1, 1,
                        1, 0, 1,
                        1, 1, 1,
                        0, 0, 1,
                        1, 1, 1}, new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1}},
                new Object[]{new double[]{
                        1, 1, 1,
                        1, 0, 1,
                        1, 0, 1,
                        1, 0, 1,
                        1, 1, 1}, new double[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0}}

        );
    }

    public PerceptronTeacherTest(double[] input, double[] expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    @Test
    public void process() throws Exception {
        System.out.println("Input: " + Arrays.toString(input));
        double[] actualOutput = network.process(input);

        boolean failed = false;
        for (int i = 0; i < actualOutput.length; i++) {
            if (Math.abs(actualOutput[i] - expectedOutput[i]) > MAX_ERROR) {
                failed = true;
            }
            System.out.format("Error[%d]: %f", i, Math.abs(actualOutput[i] - expectedOutput[i]));
        }

        Assert.assertTrue(!failed);
    }
}