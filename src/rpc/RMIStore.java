package rpc;

import service.Service;

import java.rmi.RemoteException;
import java.util.logging.Level;

public class RMIStore implements RMIService {
    private Service service;    /* service of the store */

    /**
     * Construct the store of the service
     *
     * @param service the service of the store
     */
    protected RMIStore(Service service) {
        this.service = service;
    }

    @Override
    /**
     * The service processes the request
     *
     * @param request request to process
     * @throws RemoteException
     * @return response to the request
     */
    public String process(String request, int id) {
        Service.logger.log(Level.INFO, "request received from client " + id + ": " + request);
        String response = service.process(request);
        Service.logger.log(Level.INFO,"response sent to client " + id + ": " + response);
        return response;
    }
}
