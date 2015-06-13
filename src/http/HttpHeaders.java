package http;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class HttpHeaders {

    @SuppressWarnings("rawtypes")
    private List<HashMap> hedderlist = new ArrayList<HashMap>();

    public void addHeader(String key,String value){
        HashMap<String, String> ha = new HashMap<String, String>();
        ha.put(key, value);
        this.hedderlist.add(ha);
    }

    public int getHeadderCount(){
        return hedderlist.size();
    }
}
