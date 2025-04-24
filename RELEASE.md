# Releasing a new version

* [ ] Make sure the build passed successfully
* [ ] Update the `CHANGELOG.md` file
* [ ] Update the `README.md` file (at least the version in the Installation section)
* [ ] Create the release commit (`git commit -S`)
* [ ] Create the tag (`git tag vx.y.z -s -m "Tag release vx.y.z"`)
* [ ] Merge the code in the major branch and on trunk
* [ ] Push the branches and the tag
* [ ] Bump the version in the `gradle.properties` file
