package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SumMatrixImpl implements SumMatrix {

    private final int nThreads;

    /**
     * @param nThreads
     *              number of threads performing the sum
     */
    public SumMatrixImpl(final int nThreads) {
        this.nThreads = nThreads;
    }

    private static class Worker extends Thread {
        private final double [][] matrix;
        private final int startPos;
        private final int nElem;
        private long res;

        /**
         * Build a new worker.
         * 
         * @param matrix
         *            the matrix to sum
         * @param startPos
         *            the initial position for this worker
         * @param nElem
         *            the number of elements to sum up for this worker
         */
        Worker(final double[][] matrix, final int startPos, final int nElem) {
            this.matrix = matrix;
            this.startPos = startPos;
            this.nElem = nElem;
            this.res = 0;
        }

        @Override
        public void run() {
            System.out.println("Working in line " + startPos + " to line " + (startPos + nElem - 1));
            for (int i = startPos; i < matrix.length && i < startPos + nElem; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    this.res += this.matrix[i][j];
                }
            }

        }

        /**
         * Returns the result of summing up the integers within the matrix.
         * 
         * @return the sum of the elements
         */
        public long getResult() {
            return this.res;
        }
    }

    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length % nThreads + matrix.length / nThreads;
        /* Alternativa
          final int size = matrix.length / (Math.max(2, nThreads) - 1);
         */

        final List<Worker> workers = new ArrayList<>(nThreads);
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }

        for (final Worker w: workers) {
            w.start();
        }

        long sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        return sum;
    }

}
