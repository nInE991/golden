package sample;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

public class Method implements SeachTime{
    CountDownLatch latch;
    String function;
    BigDecimal a;
    BigDecimal b;
    BigDecimal tol;
    BigDecimal x1;
    BigDecimal x2;
    BigDecimal functionx1;
    BigDecimal functionx2;
    BigDecimal r;
    String perem;
    int cond = 0;
    long iter = 0;
    int iteration;
    int itermax;
    long timeMax;
    long startTime;
    long timeLimit;
    long resultTime;
    long pauseTimeStart;

    @Override
    public void StartPause() {
        pauseTimeStart = System.currentTimeMillis();
    }

    @Override
    public void StopPause() {
        startTime = startTime + (System.currentTimeMillis() - pauseTimeStart);
    }
    public void SeachMin(){
        iter++;
        if(functionx1.compareTo(functionx2)==1){
            a=x1;
        }
        else {
            b=x2;
        }
    }
    public void SeachMax(){
        iter++;
        if(functionx1.compareTo(functionx2)==-1){
            a=x1;
        }
        else {
            b=x2;
        }
    }
    
    public BigDecimal getA(){
        return new Expression("b-((b-a)/r)").with("a",a).with("b",b).with("r",r).eval();
    }
    public BigDecimal getB(){
        return new Expression("a+((b-a)/r)").with("a",a).with("b",b).with("r",r).eval();
    }
}
