package nl.wilbertbongers.backend_eindopdracht.util;

import java.util.EnumSet;

public class EnumValidator {

    public static boolean StateTypeValidator(String state) {

        EnumSet<StateType> allStates = EnumSet.allOf(StateType.class); ;
        for (StateType entry : allStates ) {
            if (state.toUpperCase().equals(entry.name())) {
                return true;
            }
        }
        return false;
    }
    public static boolean OrderTypeValidator(String order) {
        EnumSet<OrderType> allOrderTypes = EnumSet.allOf(OrderType.class); ;
        for (OrderType entry : allOrderTypes ) {
            if (order.toUpperCase().equals(entry.name())) {
                return true;
            }
        }
        return false;
    }
}
