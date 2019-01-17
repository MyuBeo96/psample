package vinacert.vn.psample;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AppConfig {
    private java.util.Hashtable<String, String> m_propsconfig;
    public AppConfig(String[] args, String APP_ID)
    {
        if (args.length == 0)
        {
            init(getDefaultConfig());
        }
        else
        {
            init(args[0]);
        }
    }
    public AppConfig(String file_name)
    {
        init(file_name);
    }
    private String configfile;
    private synchronized void init(String configfile)
    {
        this.configfile = configfile;
        m_propsconfig = new java.util.Hashtable<String, String>();
    }
    public synchronized void clear()
    {
        if(m_propsconfig != null){
            m_propsconfig.clear();
        }
    }
    private synchronized String getDefaultConfig()
    {
        return MainApp.getInstance().getDirectory("") + "cp.properties";
    }
    public synchronized String getProperty(String key)
    {
        if(m_propsconfig.containsKey(key))
        {
            return m_propsconfig.get(key);
        }
        return "";
    }
    public synchronized void setProperty(String key, String val)
    {
        if (m_propsconfig.containsKey(key))
        {
            m_propsconfig.remove(key);
        }
        m_propsconfig.put(key, val);
    }

    public synchronized String getConfigFile()
    {
        return configfile;
    }
    public synchronized void load()
    {
        try
        {
            java.io.File yourFile = new java.io.File(configfile);
            if(yourFile.exists() == false){
                return;
            }
            InputStream in = new FileInputStream(configfile);
            java.io.DataInputStream sr = new java.io.DataInputStream(in);
            String line;
            while ((line = sr.readLine()) != null)
            {
                int index = line.indexOf("=");
                if (index != -1)
                {
                    String key = line.substring(0, index).trim();
                    if(key.length()>0){
                        if(key.charAt(0) =='.')
                        {
                            key = key.substring(1);
                        }
                    }

                    String val = line.substring(index + 1).trim();
                    m_propsconfig.put(key, val);
                }
            }
            sr.close();
            in.close();

        }catch(Exception ex)
        {

        }
    }
    public synchronized void save()
    {
        try
        {
            java.io.File yourFile = new java.io.File(configfile);
            if(yourFile.exists()){
                yourFile.delete();
            }
            OutputStream out = new FileOutputStream(configfile);
            java.io.DataOutputStream sw = new java.io.DataOutputStream(out);
            java.util.Enumeration<String> ie = m_propsconfig.keys();
            String sValue = "";
            while(ie.hasMoreElements())
            {
                String key = ie.nextElement();
                String val = m_propsconfig.get(key);
                sValue += key + "=" + val + "\r\n";
            }
            //com.bzb.MainApp.GetInstance().alert(sValue);
            sw.writeBytes(sValue);
            sw.close();
            out.close();
        }catch(Exception ex){

        }

    }
}