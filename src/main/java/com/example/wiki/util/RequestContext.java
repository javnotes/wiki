package com.example.wiki.util;

import java.io.Serializable;

/**
 * 用于保存每个请求的相关信息，比如用户信息、IP地址等，以便在业务逻辑中使用，避免在业务逻辑中传递参数。
 * 由于每个请求都是独立的，因此使用ThreadLocal来保存每个请求的相关信息。
 * ThreadLocal是一种以空间换时间的做法，它为每个线程都提供了一个变量的副本，因此每个线程都可以独立地改变自己的副本，而不会和其它线程的副本冲突。
 * ThreadLocal的使用非常简单，只需要创建一个ThreadLocal对象，然后调用它的set()和get()方法就可以了。
 * ThreadLocal的原理是，每个ThreadLocal对象都会维护一个以当前线程为key，value为任意对象的Map。
 * 当我们调用ThreadLocal的get()方法时，它首先获取当前线程，然后以当前线程为key去Map中查找对应的value，如果找到了就返回，否则就创建一个新的value并以当前线程为key保存到Map中，然后返回这个value。
 * ThreadLocal的set()方法也是类似的原理，只不过是先调用get()方法获取value，然后再调用set()方法设置value。
 * ThreadLocal的remove()方法用于删除当前线程对应的value，以便垃圾回收器回收value所占用的内存。
 * ThreadLocal的get()方法和set()方法都是以当前线程为key去Map中查找value，因此它们都是线程安全的。
 * ThreadLocal的remove()方法是以当前线程为key去Map中删除value，因此它也是线程安全的。
 * ThreadLocal的实现原理是以当前线程为key去Map中查找value，因此它的性能是非常高的，因为每个线程都只会查找一次Map，而不是像synchronized那样每次都要去争抢锁。
 * ThreadLocal的缺点是，由于每个线程都会维护一个Map，因此如果线程数量很多，那么这些Map就会占用很大的内存空间。
 * 由于ThreadLocal的实现原理是以当前线程为key去Map中查找value，因此它只能在单线程环境下使用，如果在多线程环境下使用，那么它就会失效。
 */
public class RequestContext implements Serializable {

    // 在接口入口获取请求的IP地址，然后保存到ThreadLocal中，以便在业务逻辑中使用。
    private static ThreadLocal<String> remoteAddr = new ThreadLocal<>();

    public static String getRemoteAddr() {
        return remoteAddr.get();
    }

    public static void setRemoteAddr(String remoteAddr) {
        RequestContext.remoteAddr.set(remoteAddr);
    }

}
