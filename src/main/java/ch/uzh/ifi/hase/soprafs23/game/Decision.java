package ch.uzh.ifi.hase.soprafs23.game;


public enum Decision {
    CALL, RAISE, FOLD, NOT_DECIDED;

    private Integer val;

     
    public void setRaiseValue(int x) throws Exception{ //the amount which is raised. 0 if call.
        if (this != RAISE)  {
            throw new Exception("Only Raise has a RaiseAmount");
        }
        if (val == null) {
            val = x;
        } else {
            throw new Exception("Value is already set");
        }
    }

    public int getRaiseValue() throws Exception {
        if (this != RAISE) {
            throw new Exception("Only Raise has a RaiseAmount");
        }
        return val;
    }
    

}

class Test {
    public static void main(String[] args) throws Exception {
        Decision D = Decision.CALL;
        System.out.println(D);

        Decision D2 = Decision.RAISE;
        System.out.println(D2);
        D2.setRaiseValue(100);
        System.out.println(D2.getRaiseValue());
        
        try {
            D2.setRaiseValue(200);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        try {
            D.setRaiseValue(200);
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            D.getRaiseValue();
        } catch (Error e) {
            System.out.println(e);
        }

        D = Decision.FOLD;

        try {
            D.setRaiseValue(200);
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            D.getRaiseValue();
        } catch (Error e) {
            System.out.println(e);
        }

    }
}
