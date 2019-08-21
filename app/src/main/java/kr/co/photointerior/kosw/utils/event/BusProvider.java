package kr.co.photointerior.kosw.utils.event;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Evnet bus class.
 */
public class BusProvider extends Bus {
    private static Bus instance = new Bus(ThreadEnforcer.ANY);

    public static Bus instance(){
        return instance;
    }

    private BusProvider(){}
}
