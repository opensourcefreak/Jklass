package net.opensourcefreak.jklass.api2;

public interface ApiCallback<T>  {
    void invoke(T result);
}
