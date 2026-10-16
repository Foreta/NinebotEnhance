package dev.ichinomiya.ninebotenhance.core;

/**
 * One turn-by-turn state from a phone navigation app, in the units the dashboard commands use: metres, seconds, and the
 * NaviState turn icon numbering (2 left, 3 right, 4 left-front, 5 right-front, 6 left-back, 7 right-back, 8 u-turn, 9 straight,
 * 11 / 12 roundabout, 15 arrived). {@code receivedAt} is the module service's elapsed-realtime clock when the update arrived.
 */
public record NaviUpdate(String source,int totalDistance,int remainDistance,int remainSeconds,int drivenDistance,int drivenSeconds,
                         String currentRoad,String nextRoad,int segmentRemain,int maneuver,int lights,long receivedAt,float speed) {
    public static final long FRESH_MS=8000;
    /** Speed readings go stale much faster than the route itself; the gauge falls back once they do. */
    public static final long SPEED_FRESH_MS=3000;
    /** Legacy constructor without a speed reading; speed stays unknown (-1). */
    public NaviUpdate(String source,int totalDistance,int remainDistance,int remainSeconds,int drivenDistance,int drivenSeconds,
                      String currentRoad,String nextRoad,int segmentRemain,int maneuver,int lights,long receivedAt){
        this(source,totalDistance,remainDistance,remainSeconds,drivenDistance,drivenSeconds,currentRoad,nextRoad,segmentRemain,maneuver,lights,receivedAt,-1f);
    }
    public boolean fresh(long now){return receivedAt>0&&now-receivedAt<=FRESH_MS;}
    /** Current speed in km/h when the app reported one recently; negative means unknown. */
    public float speedKmh(long now){return speed>=0&&receivedAt>0&&now-receivedAt<=SPEED_FRESH_MS?speed:-1f;}
    public boolean arrived(){return maneuver==15||remainDistance<=0&&totalDistance>0;}
    public NaviUpdate receivedAt(long at){return new NaviUpdate(source,totalDistance,remainDistance,remainSeconds,drivenDistance,drivenSeconds,currentRoad,nextRoad,segmentRemain,maneuver,lights,at,speed);}
    public String describe(){
        return source+" remain="+remainDistance+"m/"+remainSeconds+"s driven="+drivenDistance+"m/"+drivenSeconds+"s seg="+segmentRemain+"m icon="+maneuver+" lights="+lights+" cur="+currentRoad+" next="+nextRoad+" total="+totalDistance+(speed>=0?" speed="+speed:"");
    }
    static String clean(String value){return value==null?"":value.trim();}
}
