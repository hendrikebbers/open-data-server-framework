package com.openelements.data.sample.maven;

import com.openelements.data.db.AbstractEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import java.time.YearMonth;

@Entity
public class MavenPluginDownloadEntity extends AbstractEntity {

    @Basic(optional = false)
    private String plugin;

    @Basic(optional = false)
    private YearMonth time;

    @Basic(optional = false)
    private int downloadCount;

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
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
        return time + "-" + plugin;
    }
}
