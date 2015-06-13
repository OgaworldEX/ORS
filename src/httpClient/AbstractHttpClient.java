package httpClient;

import java.io.IOException;

import http.HttpMessage;
import execPlan.ExecPlanGroup;

public abstract class AbstractHttpClient {
    abstract public void sendMessage(ExecPlanGroup spg) throws IOException;
    abstract public void sendRedirectMessage(HttpMessage message) throws IOException;
    abstract protected void preparaSocket();
}
