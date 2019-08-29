package lab.itsoul.com.deliman.libshared.data.rider;

import com.itsoul.lab.domain.models.auth.UserInfo;

import java.util.List;

import lab.itsoul.com.deliman.libshared.data.DataSource;
import lab.itsoul.com.deliman.libshared.model.Rider;
import lab.itsoul.com.deliman.libshared.model.VerificationResult;

public interface RiderDataSource extends DataSource {
    VerificationResult verify(UserInfo userInfo);
    List<Rider> findRiders();
}
