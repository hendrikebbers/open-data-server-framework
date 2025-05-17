package com.openelements.data.sample;

import com.openelements.data.sample.employee.Employee;
import com.openelements.data.sample.employee.EmployeeDataTypeFactory;
import com.openelements.data.sample.employee.EmployeeProvider;
import com.openelements.data.sample.maven.MavenCoreDownloadEntity;
import com.openelements.data.sample.maven.MavenCoreDownloadEntityDataTypeFactory;
import com.openelements.data.sample.maven.MavenCoreDownloadEntityProvider;
import com.openelements.data.sample.maven.MavenPluginDownloadEntity;
import com.openelements.data.sample.maven.MavenPluginDownloadEntityDataTypeFactory;
import com.openelements.data.sample.maven.MavenPluginDownloadEntityProvider;
import com.openelements.data.sample.pullrequest.PullRequest;
import com.openelements.data.sample.pullrequest.PullRequestDataTypeFactory;
import com.openelements.data.sample.pullrequest.PullRequestProvider;
import com.openelements.data.server.DataServer;

public class Sample {

    public static void main(String[] args) {
        DataServer dataServer = new DataServer(8080);

        dataServer.addDataProvider(Employee.class, new EmployeeProvider(), 2);
        dataServer.registerEntityDefinition("employees", EmployeeDataTypeFactory.createDataType());

        dataServer.addDataProvider(MavenCoreDownloadEntity.class, new MavenCoreDownloadEntityProvider());
        dataServer.registerEntityDefinition("maven-core-downloads",
                MavenCoreDownloadEntityDataTypeFactory.createDataType());

        dataServer.addDataProvider(MavenPluginDownloadEntity.class, new MavenPluginDownloadEntityProvider());
        dataServer.registerEntityDefinition("maven-plugin-downloads",
                MavenPluginDownloadEntityDataTypeFactory.createDataType());

        dataServer.addDataProvider(PullRequest.class, new PullRequestProvider());
        dataServer.registerEntityDefinition("pullRequests", PullRequestDataTypeFactory.createDataType());

        dataServer.start();
    }


}
