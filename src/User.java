import java.io.PrintWriter;
import java.net.Socket;

public class User
{
	private String name;
	private long ping;

	private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public long getPing()
	{
		return ping;
	}
	public void setPing(long ping)
	{
		this.ping = ping;
	}


    @Override
    public String toString() {
        return name;
    }
}
