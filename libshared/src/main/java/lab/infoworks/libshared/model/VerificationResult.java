package lab.infoworks.libshared.model;

import com.google.gson.annotations.SerializedName;
import com.infoworks.lab.rest.models.Response;

public class VerificationResult extends Response {
    @SerializedName("isVerified")
    private boolean isVerified;

    public VerificationResult() {
    }

    public VerificationResult(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}
