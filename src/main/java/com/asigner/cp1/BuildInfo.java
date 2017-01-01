package com.asigner.cp1;

interface BuildInfo {
    Version getVersion();
    java.time.Instant getBuildTime();
    String getCommit();
    String getOSName();
    String getOSArch();
    String toString();
}
