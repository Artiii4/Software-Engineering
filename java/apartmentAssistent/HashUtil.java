package apartmentAssistent;

import org.mindrot.jbcrypt.BCrypt;

public class HashUtil {
    public static String hash(String getIt) {
        return BCrypt.hashpw(getIt, BCrypt.gensalt());
    }

    public static boolean check(String getIt, String hashedInfo) {
        return BCrypt.checkpw(getIt, hashedInfo);
    }
}