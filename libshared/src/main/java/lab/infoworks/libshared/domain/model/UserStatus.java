package lab.infoworks.libshared.domain.model;

public class UserStatus {
    private boolean isVerified = false;

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}
