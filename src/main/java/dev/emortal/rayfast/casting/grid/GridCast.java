package dev.emortal.rayfast.casting.grid;

import dev.emortal.rayfast.vector.Vector3d;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class GridCast {

    /**
     * Creates an iterator that iterates through blocks on a 3d 1x1 grid forever.
     *
     * @param startX iterator start position X
     * @param startY iterator start position Y
     * @param startZ iterator start position Z
     * @param dirX iterator direction X
     * @param dirY iterator direction Y
     * @param dirZ iterator direction Z
     * @return the iterator
     */
    public static GridIterator createGridIterator(
            double startX,
            double startY,
            double startZ,
            double dirX,
            double dirY,
            double dirZ
    ) {
        return createGridIterator(startX, startY, startZ, dirX, dirY, dirZ, 1.0, Double.MAX_VALUE);
    }

    /**
     * Creates an iterator that iterates through blocks on a 3d 1x1 grid forever.
     *
     * @param start iterator start position
     * @param dir iterator direction
     * @return the iterator
     */
    public static GridIterator createGridIterator(
            Vector3d start,
            Vector3d dir
    ) {
        return createGridIterator(start.x(), start.y(), start.z(), dir.x(), dir.y(), dir.z(), 1.0, Double.MAX_VALUE);
    }

    /**
     * Creates an iterator that iterates through blocks on a 3d grid of the specified grid size, until the total length
     * exceeds the length specified.
     *
     * @param startX iterator start position X
     * @param startY iterator start position Y
     * @param startZ iterator start position Z
     * @param dirX iterator direction X
     * @param dirY iterator direction Y
     * @param dirZ iterator direction Z
     * @param gridSize the size of the grid
     * @param length the maximum length of the iterator
     * @return the iterator
     */
    public static GridIterator createGridIterator(
            double startX,
            double startY,
            double startZ,
            double dirX,
            double dirY,
            double dirZ,
            double gridSize,
            double length
    ) {
        return new GridIterator(startX, startY, startZ, dirX, dirY, dirZ, gridSize, length);
    }

    /**
     * Creates an iterator that iterates through blocks on a 3d grid of the specified grid size, until the total length
     * exceeds the length specified.
     *
     * @param start iterator start position
     * @param dir iterator direction
     * @param gridSize the size of the grid
     * @param length the maximum length of the iterator
     * @return the iterator
     */
    public static GridIterator createGridIterator(
            Vector3d start,
            Vector3d dir,
            double gridSize,
            double length
    ) {
        return new GridIterator(start.x(), start.y(), start.z(), dir.x(), dir.y(), dir.z(), gridSize, length);
    }

    /**
     * Creates an iterator that iterates through blocks on a 3d grid of the specified grid size, giving the exact
     * position that was hit when any grid unit was intersected. It does this until the total length exceeds the length
     * specified.
     *
     * @param start iterator start position
     * @param dir iterator direction
     * @param gridSize the size of the grid
     * @param length the maximum length of the iterator
     * @return the iterator
     */
    public static GridIterator createExactGridIterator(
            Vector3d start,
            Vector3d dir,
            double gridSize,
            double length
    ) {
        return new ExactGridIterator(start.x(), start.y(), start.z(), dir.x(), dir.y(), dir.z(), gridSize, length);
    }

    /**
     * Creates an iterator that iterates through blocks on a 3d grid of the specified grid size, giving the exact
     * position that was hit when any grid unit was intersected. It does this until the total length exceeds the length
     * specified.
     *
     * @param startX iterator start position X
     * @param startY iterator start position Y
     * @param startZ iterator start position Z
     * @param dirX iterator direction X
     * @param dirY iterator direction Y
     * @param dirZ iterator direction Z
     * @param gridSize the size of the grid
     * @param length the maximum length of the iterator
     * @return the iterator
     */
    public static GridIterator createExactGridIterator(
            double startX,
            double startY,
            double startZ,
            double dirX,
            double dirY,
            double dirZ,
            double gridSize,
            double length
    ) {
        return new ExactGridIterator(startX, startY, startZ, dirX, dirY, dirZ, gridSize, length);
    }

    private static class GridIterator implements Iterator<Vector3d>, Iterable<Vector3d> {

        protected double posX;
        protected double posY;
        protected double posZ;
        protected final double dirX;
        protected final double dirY;
        protected final double dirZ;
        protected final double gridSize;
        protected final double length;
        protected double currentLength = 0;

        protected GridIterator(
                double startX,
                double startY,
                double startZ,
                double dirX,
                double dirY,
                double dirZ,
                double gridSize,
                double length
        ) {
            this.posX = startX;
            this.posY = startY;
            this.posZ = startZ;
            this.dirX = dirX;
            this.dirY = dirY;
            this.dirZ = dirZ;
            this.gridSize = gridSize;
            this.length = length;
        }

        @Override
        public boolean hasNext() {
            return currentLength < length;
        }

        @Override
        public Vector3d next() {
            // Find the length to the next block
            double lengthX = (gridSize - (Math.abs(posX) % gridSize)) / Math.abs(dirX);
            double lengthY = (gridSize - (Math.abs(posY) % gridSize)) / Math.abs(dirY);
            double lengthZ = (gridSize - (Math.abs(posZ) % gridSize)) / Math.abs(dirZ);

            // Find the lowest of all
            double lowest = Math.min(lengthX, Math.min(lengthY, lengthZ));

            // Cast to the next block
            this.posX += dirX * lowest;
            this.posY += dirY * lowest;
            this.posZ += dirZ * lowest;

            // Add length to current
            currentLength += lowest;

            return Vector3d.of(
                    posX - posX % gridSize,
                    posY - posY % gridSize,
                    posZ - posZ % gridSize
            );
        }

        @Override
        public @NotNull Iterator<Vector3d> iterator() {
            return this;
        }
    }

    private static class ExactGridIterator extends GridIterator {
        protected ExactGridIterator(double startX, double startY, double startZ, double dirX, double dirY, double dirZ, double gridSize, double length) {
            super(startX, startY, startZ, dirX, dirY, dirZ, gridSize, length);
        }

        @Override
        public Vector3d next() {
            // Find the length to the next block
            double lengthX = (gridSize - (Math.abs(posX) % gridSize)) / Math.abs(dirX);
            double lengthY = (gridSize - (Math.abs(posY) % gridSize)) / Math.abs(dirY);
            double lengthZ = (gridSize - (Math.abs(posZ) % gridSize)) / Math.abs(dirZ);

            // Find the lowest of all
            double lowest = Math.min(lengthX, Math.min(lengthY, lengthZ));

            // Cast to the next block
            this.posX += dirX * lowest;
            this.posY += dirY * lowest;
            this.posZ += dirZ * lowest;

            // Add length to current
            currentLength += lowest;

            return Vector3d.of(
                    posX,
                    posY,
                    posZ
            );
        }
    }


}
