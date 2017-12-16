package ru.vpcb.builditbigger;

public interface ICallback {
    void onComplete(String s);
    void onComplete(int value);
    void onCompleteIdling();
}