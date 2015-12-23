# easymargining

## 1 / Installation procedure

Unzip open/gamma-eurex/pme-18.3_eurex-repository.zip on your drive.

## 1.1 / Maven configuration : To add on settings.xml
## Add the profile below in profiles tag
	<profile>
      <id>eurex-pme-profile</id>
        <repositories>
          <repository>
            <id>eurex-pme</id>
            <url>file://[Path of pme-1.8.3_eurex-repository]</url>
          </repository>
        </repositories>
    </profile>
    <profile>
        <id>easymargining</id>
        <repositories>
            <repository>
                <id>easymargining</id>
                <url>file://[Path of easymargin-local-repository]</url>
            </repository>
        </repositories>
        <pluginRepositories>
            <pluginRepository>
                <id>easymargining-plugins</id>
                <url>file://[Path of easymargin-local-repository]</url>
            </pluginRepository>
        </pluginRepositories>
    	</profile>
## Add the activeProfile below in activeProfiles tag
    <activeProfile>eurex-pme-profile</activeProfile>

## 1.2 Lombok
https://projectlombok.org/

## Python for Client tests
https://www.continuum.io/downloads

## Git utils tools
https://desktop.github.com/