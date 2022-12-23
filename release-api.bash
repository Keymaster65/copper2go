# 1) Create artifact
# 1.1) Update release info and move from "ongoing" to "Released"
# 1.1) Update version in copper2go-api/build.gradle.kts and this file
# 1.2) publishToMavenLocal
# 1.3) Commit and push to master
# 2) Signing
# 2.1) Start this script
# 2.2) Enter passphrase for CA-Key (Kleopatra)
# 2.3) If key is expired visit: https://central.sonatype.org/publish/requirements/gpg/#locate-and-examine-your-staging-repository
# 3) Upload at https://oss.sonatype.org/#nexus-search;quick~copper2go
# 3.1) Login as keymaster65 with Password sonartype
# 3.2) Select "Staging Upload" or https://oss.sonatype.org/#staging-upload
# 3.3) Select "Artifact Bundle" as "Upload Mode"
# 3.4) Select local "bundle.jar" and click "Upload Bundle"
# 3.5) Check "staging Repositories"
# 3.6) Check not in "https://repo1.maven.org/maven2/io/github/keymaster65/copper2go-api/"
# 4) Press "Release" (in der Überschriftsleiste, erscheint ggfls. verzögert)
# 4.1) Enter discription in next dialog (may be the release notes title)
# 4.2) Upload at https://oss.sonatype.org/#nexus-search;quick~copper2go
# 4.3) Check later "https://search.maven.org/search?q=a:copper2go-api"
# 4.4) Check later "https://repo1.maven.org/maven2/io/github/keymaster65/copper2go-api/"
# 5) Tag
# 5.1) create tag in idea or git tag v3.2.0-api
# 5.2) git push origin v3.2.0-api -f
 

v=3.2.0
mkdir $v
cd $v
cp ~/.m2/repository/io/github/keymaster65/copper2go-api/$v/*.* .
rm *.module
for f in *.*; do gpg.exe -a --detach-sign $f ; done
jar -cvf ../bundle.jar *.*
