package tap.execounting.util;

import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Payment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * User: truth0
 * Date: 3/30/13
 * Time: 12:51 PM
 */
public class Trans {
    public static List<Contract> clientsToContracts(List<Client> clients){
        List<Contract> list = new ArrayList<>();
        for(Client c : clients)
            list.addAll(c.getContracts());
        return list;
    }
    public static List<Client> contractsToClients(List<Contract> contracts){
        HashSet<Client> set = new HashSet<>();
        for(Contract c : contracts)
            set.add(c.getClient());
        return new ArrayList<>(set);
    }

    public static List<Payment> contractsToPayments(List<Contract> contracts){
        List<Payment> list = new ArrayList<>();
        for(Contract con : contracts)
            list.addAll(con.getPayments());
        return list;
    }

    public static List<Payment> clientsToPayments(List<Client> clients) {
        List<Payment> list = new ArrayList<>();
        for(Client glen : clients)
            list.addAll(contractsToPayments(glen.getContracts()));
        return list;
    }

    public static List<Client> paymentsToClients(List<Payment> payments) {
        HashSet<Client> set = new HashSet<>();
        for(Payment p : payments)
            set.add(p.getContract().getClient());
        return new ArrayList<>(set);
    }
}
