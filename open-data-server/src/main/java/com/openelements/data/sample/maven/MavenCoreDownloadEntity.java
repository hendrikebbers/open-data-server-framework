package com.openelements.data.sample.maven;

import com.openelements.data.db.AbstractEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import java.time.YearMonth;

@Entity
public class MavenCoreDownloadEntity extends AbstractEntity {

    @Basic(optional = false)
    private String version;

    @Basic(optional = false)
    private YearMonth time;

    @Basic(optional = false)
    private int downloadCount;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public YearMonth getTime() {
        return time;
    }

    public void setTime(YearMonth time) {
        this.time = time;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    @Override
    protected String calculateUUID() {
        return time + "-" + version;
    }
}
