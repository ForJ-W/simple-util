package com.simple.util.factory;

import com.simple.util.strategy.POIProcess;
import com.simple.util.strategy.impl.EntityPOIProcess;
import com.simple.util.strategy.impl.MapPOIProcess;

import java.util.Map;

/**
 * @author wujing
 * @date 2020/5/10 13:46
 * @see POIProcess
 */
@SuppressWarnings("unchecked")
public final class POIProcessFactory {

    private volatile static MapPOIProcess mapPOIProcess;
    private volatile static EntityPOIProcess entityPOIProcess;

    private POIProcessFactory() {
    }

    public static <D> POIProcess<D> getInstance(Class<D> dataClass) {

        if (Map.class.isAssignableFrom(dataClass)) {
            if (null == mapPOIProcess) {
                synchronized (POIProcess.LOCK) {
                    if (null == mapPOIProcess) {

                        mapPOIProcess = new MapPOIProcess<D>();
                    }
                }
            }
            return mapPOIProcess;
        } else {
            if (null == entityPOIProcess) {
                synchronized (POIProcess.LOCK) {
                    if (null == entityPOIProcess) {

                        entityPOIProcess = new EntityPOIProcess<D>();
                    }
                }
            }
            return entityPOIProcess;
        }
    }
}
