# 1) publishToMavenLocal 
# 2) Enter passphrase for CA-Key (Kleopatra)
# 3) Upload at https://oss.sonatype.org/#nexus-search;quick~copper2go
# 3.1) Login as keymaster65 with Password sonartype
# 3.2) Select "Staging Upload" or https://oss.sonatype.org/#staging-upload
# 3.3) Select "Artifact Bundle" as "Upload Mode"
# 3.4) Select local "bundle.jar" and click "Upload Bundle"
# 3.5) Check "staging Repositories"
# 3.6) Check not in "https://repo1.maven.org/maven2/io/github/keymaster65/copper2go-api/"
# 4) Press "Release" (in der Überschriftsleiste) and enter discription (may be the release notes title)
# 4.1) Upload at https://oss.sonatype.org/#nexus-search;quick~copper2go
# 4.2) Check later "https://search.maven.org/search?q=a:copper2go-api"
# 4.3) Check later "https://repo1.maven.org/maven2/io/github/keymaster65/copper2go-api/"
# 5) Tag
# 5.1) create tag in idea or git tag v3.2.0-api
# 5.2) git push origin v3.2.0-api -f
 

v=3.1.1
mkdir $v
cd $v
cp ~/.m2/repository/io/github/keymaster65/copper2go-api/$v/*.* .
rm *.module
for f in *.*; do gpg.exe -a --detach-sign $f ; done
jar -cvf ../bundle.jar *.*
