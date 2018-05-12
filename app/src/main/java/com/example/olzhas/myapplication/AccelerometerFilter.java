package com.example.olzhas.myapplication;

public class AccelerometerFilter {
    // ref: http://timesfun.net/chudinhbka/lifelog_android/raw/c25bb19fce157dfe6c4b61fc93afd614a1ec8c84/app/src/main/java/com/dinhcv/lifelogpedometer/activity/SensorFilter.java
    private static final int ACCEL_RING_SIZE = 50;
    private static final int VEL_RING_SIZE = 10;
    private int accelRingCounter = 0;
    private float[] accelRingX = new float[ACCEL_RING_SIZE];
    private float[] accelRingY = new float[ACCEL_RING_SIZE];
    private float[] accelRingZ = new float[ACCEL_RING_SIZE];
    private int velRingCounter = 0;
    private float[] velRing = new float[VEL_RING_SIZE];

    private float sum(float[] array) {
        float retval = 0;
        for (int i = 0; i < array.length; i++) {
            retval += array[i];
        }
        return retval;
    }

    private float norm(float[] array) {
        float retval = 0;
        for (int i = 0; i < array.length; i++) {
            retval += array[i] * array[i];
        }
        return (float) Math.sqrt(retval);
    }


    private float dot(float[] a, float[] b) {
        float retval = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
        return retval;
    }

    public float estimateVelocity(float[] acceleration) {
        accelRingCounter++;
        accelRingX[accelRingCounter % ACCEL_RING_SIZE] = acceleration[0];
        accelRingY[accelRingCounter % ACCEL_RING_SIZE] = acceleration[1];
        accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = acceleration[2];

        float[] worldZ = new float[3];
        worldZ[0] = sum(accelRingX) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[1] = sum(accelRingY) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[2] = sum(accelRingZ) / Math.min(accelRingCounter, ACCEL_RING_SIZE);

        float normalizationFactor = norm(worldZ);
        worldZ[0] = worldZ[0] / normalizationFactor;
        worldZ[1] = worldZ[1] / normalizationFactor;
        worldZ[2] = worldZ[2] / normalizationFactor;

        float currentZ = dot(worldZ, acceleration) - normalizationFactor;
        velRingCounter++;
        velRing[velRingCounter % VEL_RING_SIZE] = currentZ;
        return sum(velRing);
    }
}
