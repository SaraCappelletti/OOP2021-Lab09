package it.unibo.oop.lab.workers02;

import java.util.stream.IntStream;

/**
 *
 */
public class SumMatrixWithStreamsImpl implements SumMatrix {

    private final int nThreads;

    /**
     * @param nThreads
     *              number of threads performing the sum
     */
    public SumMatrixWithStreamsImpl(final int nThreads) {
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

        return IntStream.iterate(0, start -> start + size)
                .limit(nThreads)
                .mapToObj(start -> new Worker(matrix, start, size))
                // Start them
                .peek(Thread::start)
                // Join them
                .peek(SumMatrixWithStreamsImpl::joinUninterruptibly)
                 // Get their result and sum
                .mapToLong(Worker::getResult)
                .sum();
    }

    private static void joinUninterruptibly(final Thread target) {
        var joined = false;
        while (!joined) {
            try {
                target.join();
                joined = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
