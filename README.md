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
## Add the activeProfile below in activeProfiles tag
    <activeProfile>eurex-pme-profile</activeProfile>

## 1.2 Lombok
https://projectlombok.org/

## Python for Client tests
https://www.continuum.io/downloads