import java.lang.annotation.Target;
import java.net.InetAddress;

public class TargetServerInfo {

    InetAddress targetIP;

    private int targetPort;

    public TargetServerInfo(){

        this.setTargetIP(null);
        this.setTargetPort(-1);
    }

    public TargetServerInfo(InetAddress targetIP, int targetPort){

        this.targetIP = targetIP;
        this.targetPort = targetPort;
    }

    public InetAddress getTargetIP() {

        return targetIP;
    }

    public int getTargetPort() {

        return targetPort;
    }

    public void setTargetIP(InetAddress targetIP) {

        this.targetIP = targetIP;
    }

    public void setTargetPort(int targetPort) {

        this.targetPort = targetPort;
    }
}
