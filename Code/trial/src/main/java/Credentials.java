public class Credentials {
    private static Credentials cred;
    String accessKey= null;
    String secretKey= null;
    private Credentials(){
        accessKey="AKIAI7J3YN5A2J2CM5UQ";
        secretKey="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";
    }
    public static synchronized Credentials getCred(){
        if(cred == null){
            cred = new Credentials();
            System.out.println("Credential object is successfully created!");
        }
        return cred;
    }
}
/*
Here using synchronized makes sure that only one thread at a time can execute getInstance().
The main disadvantage of this is method is that using synchronized every time while creating the singleton object is expensive and may decrease the performance of your program. However if performance of getInstance() is not critical for your application this method provides a clean and simple solution.
 */