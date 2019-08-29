package lab.itsoul.com.deliman.libshared.model;

import com.google.gson.annotations.SerializedName;
import com.itsoul.lab.domain.base.Produce;

public class VerificationResult extends Produce {
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
