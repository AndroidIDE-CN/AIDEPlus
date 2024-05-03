package io.github.zeroaicy.aide.ui.services;

import androidx.annotation.Keep;
import com.aide.common.AppLog;
import com.aide.ui.App;
import com.aide.ui.AppPreferences;
import com.aide.ui.util.BuildGradle;
import com.aide.ui.util.FileSystem;
import com.aide.ui.util.MavenDependencyVersion;
import com.aide.ui.util.MavenMetadataXml;
import com.aide.ui.util.PomXml;
import com.aide.ui.util.PomXml.ArtifactNode;
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 更新底包时，再优化，那时必须抽离出修改点，只保留底包对其引用的api
 */
public class ZeroAicyMavenService {

	// 返回 未下载的依赖
    public List<BuildGradle.MavenDependency> getNoDownloadDeps(Map<String, String> flatRepoPathMap, BuildGradle.MavenDependency dep) {
        try {
            List<BuildGradle.MavenDependency> noDownloadDeps = new ArrayList<>();
			ArtifactNode artifactNode = PomXml.ArtifactNode.pack(dep);
            yS(flatRepoPathMap, artifactNode, noDownloadDeps, 3);

            return noDownloadDeps;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }



	/**
	 * 共有api
	 */
	// getMetadataUrl
	@Deprecated
    public static String rN(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency) {
        try {
            return getMetadataUrl(remoteRepository, dependency);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	public static String getMetadataUrl(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency) {
        try {
            return remoteRepository.repositorieURL + ("/" + dependency.groupId.replace('.', '/') + "/" + dependency.artifactId) + "/maven-metadata.xml";
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	// getMetadataPath[返回metadata文件]
	@Deprecated
    public static String lg(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency) {
        try {
            return getMetadataPath(remoteRepository, dependency);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	public static String getMetadataPath(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency) {
        try {
			// getDefaulRepositoriePath应该叫默认下载maven缓存路径
            return getDefaulRepositoriePath() + ("/" + dependency.groupId.replace('.', '/') + "/" + dependency.artifactId) + "/maven-metadata.xml";
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }


	// 刷新maven缓存
    public void refreshMavenCache() {
        try {
            App.sy(App.gn(), "Refreshing...", new Runnable(){
					@Override
					public void run() {
						//重置依赖缓存映射
						resetDepPathMap();
						//删除 maven缓存
						FileSystem.VH(getDefaulRepositoriePath());
					}
				}, new Runnable(){
					@Override
					public void run() {
						App.getProjectService().CU();
					}
				});
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	//重置当前服务的依赖记录
	public void resetDepMap() {
        try {
            this.depMap.clear();
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	/**
	 * 静态方法
	 */
	public static String getArtifactUrl(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency, String version, String type) {
        try {
            return remoteRepository.repositorieURL + (("/" + dependency.groupId.replace('.', '/') + "/" + dependency.artifactId) + "/" + version + "/" + dependency.artifactId + "-" + version) + type;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	public static String getArtifactPath(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency, String version, String type) {
        try {
            return getDefaulRepositoriePath() + (("/" + dependency.groupId.replace('.', '/') + "/" + dependency.artifactId) + "/" + version + "/" + dependency.artifactId + "-" + version) + type;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	/**
	 * 底包引用的API
	 */

	// 返回 未下载的依赖
	@Deprecated
    public List<BuildGradle.MavenDependency> er(Map<String, String> flatRepoPathMap, BuildGradle.MavenDependency dependency) {
        try {
			return getNoDownloadDeps(flatRepoPathMap, dependency);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	// 重置依赖在maven缓存路径中的映射
	// FH() -> resetDepPathMap
	@Keep
	public void resetDepPathMap() {
        try {
            this.depPathMap.clear();
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	// 所有gradle文件中显示声明的依赖
	// 按照依赖顺序，依次传入
	// 这意味着，可以获得所有显示依赖
	// 解析MavenDependency的子依赖
	@Keep
	public void resolvingDependency(BuildGradle.MavenDependency dependency) {
        try {
			//解析并填充此依赖
            Zo(dependency, 3);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	/**
	 * 返回此依赖及其子依赖在maven缓存仓库中的路径
	 * 编译时的依赖解析
	 */
	@Deprecated
	@Keep
	public List<String> resolveFullDependencyTree(Map<String, String> flatRepositoryPathMap, BuildGradle.MavenDependency dep) {
        try {
            return Ws(flatRepositoryPathMap, resolveMavenDepPath(flatRepositoryPathMap, makeDep(dep)));
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	/**
	 * 返回当前依赖的maven缓存路径
	 * 但不会查找flatDir
	 */
	@Keep
	public String resolveMavenDepPath(BuildGradle.MavenDependency dependency) {
        try {
            return resolveMavenDepPath(null, dependency);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	/**
	 * 从给定的依赖路径，返回其自己及子依赖路径
	 * 递归3层
	 */
	@Deprecated
	@Keep
	public List<String> resolveFullDependencyTree(String depPath) {
        try {
            return Ws(null, depPath);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }



	/**
	 * 此依赖是否存在本地缓存 hasMavenCache
	 */
	@Keep
	public boolean existsLocalMavenCache(Map<String, String> flatRepoPathMap, BuildGradle.MavenDependency dependency) {
        try {
            return resolveMavenDepPath(flatRepoPathMap, makeDep(dependency)) != null;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	/**
	 * 私有，内部实现
	 */
    private Map<String, PomXml.ArtifactNode> depMap;

	// 依赖与此依赖本地仓库地址
    private Map<BuildGradle.MavenDependency, String> depPathMap;
	public static Map<BuildGradle.MavenDependency, String> getDependencyPathMap(ZeroAicyMavenService mavenService) {
		return mavenService.depPathMap;
	}

    public ZeroAicyMavenService() {
		this.depPathMap = new HashMap<>();
		this.depMap = new HashMap<>();
    }

	/**
	 * 返回此依赖的 jar(jar) | aar(aar) | pom(bom) 文件路径
	 */
	//计算缓存路径
	private String getArtifactCachePath(String repositoriePath, String groupId, String artifactId, String version, String packaging) {
        String artifactIdDir = repositoriePath + "/" + groupId.replace(".", "/") + "/" + artifactId;

		if (!FileSystem.isDirectory(artifactIdDir)) {
			return null;
		}
		//搜索本地仓库已有的版本
		//按道理不应该这样
        String searchVersion = this.searchLocalDepVersion(artifactIdDir, version);
		if (searchVersion == null) {
            return null;
        }

		version = searchVersion;

        String artifactIdVersionDir = artifactIdDir + "/" + version + "/" + artifactId + "-" + version;
		if ("pom".equals(packaging)) {
			if (new File(artifactIdVersionDir + ".pom").isFile()) {
				return artifactIdVersionDir + ".pom";
			}
		}
        if (new File(artifactIdVersionDir + ".jar").isFile()) {
            return artifactIdVersionDir + ".jar";
        }
        if (new File(artifactIdVersionDir + ".aar").isDirectory()) {
            return artifactIdVersionDir + ".aar";
        }
        if (new File(artifactIdVersionDir + ".exploded.aar").isDirectory()) {
            return artifactIdVersionDir + ".exploded.aar";
        }
        if (new File(artifactIdVersionDir + ".aar").isFile()) {
            extractedAar(artifactIdVersionDir + ".aar", artifactIdVersionDir + ".exploded.aar");
            return artifactIdVersionDir + ".exploded.aar";
        }
        return null;
    }


	// 查询是否已有依赖或者版本控制
	// 即所有依赖引用都在这，方便更新版本
    private PomXml.ArtifactNode makeDep(BuildGradle.MavenDependency dep) {
        try {

            PomXml.ArtifactNode cache = this.depMap.get(dep.getGroupIdArtifactId());
			if (cache == null) {
				cache = PomXml.ArtifactNode.pack(dep);
				this.depMap.put(dep.getGroupIdArtifactId(), cache);
            }
			return cache;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }


    private boolean P8(String str, String str2) {
        try {
			if (!new File(str2).isDirectory()) {
				return false;
			}
			File[] listFiles = new File(str2).listFiles();
			if (listFiles == null) {
				return true;
			}
			long lastModified = new File(str).lastModified();
			for (File file : listFiles) {
				if (file.isFile() && file.lastModified() < lastModified) {
					return false;
				}
			}
			return true;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }


    private void QX(Map<String, String> flatRepositoryPathMap, String str, List<String> depPaths, int depth) {
        try {
            if (depPaths.contains(str)) {
                return;
            }
            depPaths.add(str);
			if (depth < 1) {
				return;
			}

			String depPomPath = getDepPomPath(str);

			PomXml curPomXml = PomXml.empty.getConfiguration(depPomPath);

			for (PomXml.ArtifactNode artifactNode : curPomXml.depManages) {
				// dependencyManagement只做版本控制
				PomXml.ArtifactNode cache = makeDep(artifactNode);
				if (artifactNode != cache) {
					//控制版本里的更新
					if (MavenDependencyVersion.compareTo(artifactNode.version, cache.version) > 0) {
						this.depMap.put(artifactNode.getGroupIdArtifactId(), artifactNode);
					}
				}
			}
			PomXml.ArtifactNode curArtifactNode = makeDep(new PomXml.ArtifactNode(curPomXml));
			Set<String> exclusionSet = curArtifactNode.getExclusionSet();

			for (PomXml.ArtifactNode subArtifactNode : curPomXml.deps) {
				if (exclusionSet.contains(subArtifactNode.getGroupIdArtifactId())) {
					continue;
				}
				// 不知道为什么要这个
				if (vy(subArtifactNode)) {
					continue;
				}
				// 计算dependency的地址
				String depPath = resolveMavenDepPath(flatRepositoryPathMap, makeDep(subArtifactNode));
				if (depPath != null) {
					QX(flatRepositoryPathMap, depPath, depPaths, depth - 1);
				}
			}
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private void extractedAar(String aarPath, String outDir) {
        try {
            if (P8(aarPath, outDir)) {
                return;
            }
            try {
                FileSystem.u7(new FileInputStream(aarPath), outDir, true);
                AppLog.DW("Extracted AAR " + aarPath);
            }
			catch (IOException e) {
                e.printStackTrace();
            }
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	/**
	 * 编译时
	 */
    private List<String> Ws(Map<String, String> flatRepositoryPathMap, String depPath) {
        try {
            ArrayList<String> arrayList = new ArrayList<>();
            if (depPath != null) {
                QX(flatRepositoryPathMap, depPath, arrayList, 3);
            }
            return arrayList;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	// 解析gradle中的显示声明
	private void Zo(BuildGradle.MavenDependency dep, int depth) {
        try {
			//从缓存仓库计算MavenDependency依赖路径[pom|jar|aar]
            String depPath = resolveMavenDepPath(null, dep);
			if (depPath == null) {
				// 没有此依赖未下载
				return;
			}

			// 当前依赖路径的实例版本
			{
				String version = getVersion(depPath);

				BuildGradle.MavenDependency cache = this.depMap.get(dep.getGroupIdArtifactId());
				if (cache == null
					|| MavenDependencyVersion.compareTo(version, cache.version) > 0) {

					PomXml.ArtifactNode mavenDependency = new PomXml.ArtifactNode(dep, version);
					this.depMap.put(dep.getGroupIdArtifactId(), mavenDependency);
				}
			}


			if (depth < 1) {
				return;
			}
			String depPomPath = getDepPomPath(depPath);
			PomXml curPomXml = PomXml.empty.getConfiguration(depPomPath);


			for (PomXml.ArtifactNode subArtifactNode : curPomXml.depManages) {
				// dependencyManagement只做版本控制
				PomXml.ArtifactNode cache = makeDep(subArtifactNode);
				if (subArtifactNode != cache) {
					//控制版本里的更新
					if (MavenDependencyVersion.compareTo(subArtifactNode.version, cache.version) > 0) {
						this.depMap.put(subArtifactNode.getGroupIdArtifactId(), subArtifactNode);
					}
				}
			}

			PomXml.ArtifactNode curArtifactNode = makeDep(new PomXml.ArtifactNode(curPomXml));
			Set<String> exclusionSet = curArtifactNode.getExclusionSet();

			for (PomXml.ArtifactNode subArtifactNode : curPomXml.deps) {
				if (exclusionSet.contains(subArtifactNode.getGroupIdArtifactId())) {
					continue;
				}
				if (!vy(subArtifactNode)) {
					Zo(subArtifactNode, depth - 1);
				}
			}

        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }    
	//获取此依赖缓存路径的版本
    private static String getVersion(String str) {
        try {
            String[] split = str.split("/");
            return split[split.length - 2];
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }



	//从 jar|aar依赖路径返回pom路径
    private String getDepPomPath(String str) {
        try {
            if (str.endsWith(".exploded.aar")) {
                return str.substring(0, str.length() - 13) + ".pom";
            }
            return str.substring(0, str.length() - 4) + ".pom";
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	/**
	 * 默认下载maven仓库路径
	 */
	// DW() -> getDefaulRepositoriePath
    public static String getDefaulRepositoriePath() {
        String userM2Repositories = ZeroAicyExtensionInterface.getUserM2Repositories();
        if (userM2Repositories != null) {
            return userM2Repositories;
        }
        try {
            return FileSystem.yS() + "/.aide/maven";
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private String getMavenDependencyPath(BuildGradle.MavenDependency dependency) {
        try {
            for (String repositoriePath : getRepositoriePaths()) {
                String depPath = getArtifactCachePath(repositoriePath, dependency.groupId, dependency.artifactId, dependency.version, dependency.packaging);
                if (depPath != null) {
                    return depPath;
                }
            }
            return null;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private List<String> getRepositoriePaths() {
        try {
            ArrayList<String> arrayList = new ArrayList<>();
            for (String str : AppPreferences.br().split(";")) {
                if (!str.trim().isEmpty()) {
                    arrayList.add(str.trim());
                }
            }
            arrayList.add(getDefaulRepositoriePath());
            return arrayList;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	private static MavenMetadataXml mavenMetadataXml = new MavenMetadataXml();

    private String searchLocalDepVersion(String artifactIdDir, String searchVersion) {
        try {
            String metadataPath = artifactIdDir + "/maven-metadata.xml";



			String version;
			if (!FileSystem.isFileAndNotZip(metadataPath) 
				|| (version = mavenMetadataXml.getConfiguration(metadataPath).getVersion(searchVersion)) == null) {
                try {
					//一但metadata找不到查找的版本
					//则 ls metadata同级目录
					//本地所有版本集合
                    ArrayList<String> versions = new ArrayList<>();
                    for (String artifactIdVersionDir : FileSystem.we(artifactIdDir)) {
                        versions.add(FileSystem.getName(artifactIdVersionDir));
                    }
                    return MavenMetadataXml.getVersion(versions, searchVersion);
                }
				catch (Exception unused) {
                    return null;
                }
            }
            return version;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	/**
	 * 计算 flat仓库的aar[groupId无效]
	 */
    private String getFlatArtifactPath(String flatRepositoryPath, String groupId, String artifactId, String version) {
        try {
			//从
            String artifactPath = flatRepositoryPath + "/" + artifactId + ".aar";
            if (new File(artifactPath).exists()) {
                return artifactPath;
            }
            File[] listFiles = new File(flatRepositoryPath).listFiles();
            if (listFiles == null) {
				return null;
			}
			for (File file : listFiles) {
				String name = file.getName();

				if (name.startsWith(artifactId + "-") 
					&& name.endsWith(".aar")
				//是否匹配版本
					&& MavenMetadataXml.matchVersion(name.substring(artifactId.length() + 1, name.length() - 4), version)) {
					return file.getPath();
				}
			}
			return null;

        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }




    private boolean vy(BuildGradle.MavenDependency dependency) {
        try {
            return dependency.artifactId.contains("android-all");
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	/**
	 * 计算依赖在maven缓存的路径
	 */
    private String resolveMavenDepPath(Map<String, String> flatRepoPathMap, BuildGradle.MavenDependency dependency) {
        try {
            if (flatRepoPathMap != null) {
                for (String flatRepositoryPath : flatRepoPathMap.keySet()) {
                    String flatArtifactPath = getFlatArtifactPath(flatRepositoryPath, dependency.groupId, dependency.artifactId, dependency.version);
					if (flatArtifactPath != null) {
                        String name = new File(flatArtifactPath).getName();
                        String flatRepoCachePath = flatRepoPathMap.get(flatRepositoryPath);
						String explodedAarPath = flatRepoCachePath + "/" + name.substring(0, name.length() - 4) + ".exploded.aar";
                        //提取aar并解压
						extractedAar(flatArtifactPath, explodedAarPath);
                        return explodedAarPath;
                    }
                }
            }

            if (this.depPathMap.containsKey(dependency)) {
                String depPath = this.depPathMap.get(dependency);
                if (depPath.length() == 0) {
                    return null;
                }
                return depPath;
            }
            String dependencyPath = getMavenDependencyPath(dependency);
            if (dependencyPath == null) {
                this.depPathMap.put(dependency, "");
            } else {
                this.depPathMap.put(dependency, dependencyPath);
            }
            return dependencyPath;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	// 递归查找依赖缓存，不存在时加入list
    private void yS(Map<String, String> flatRepoPathMap, PomXml.ArtifactNode dependency, List<BuildGradle.MavenDependency> dependencyList, int depth) {
        try {
			//从缓存仓库计算MavenDependency依赖路径[pom|jar|aar]
            String dependencyPath = resolveMavenDepPath(flatRepoPathMap, makeDep(dependency));

			if (dependencyPath == null) {
                dependencyList.add(dependency);
			} else if (depth > 0) {
                String depPomPath = getDepPomPath(dependencyPath);

				PomXml curPomXml = PomXml.empty.getConfiguration(depPomPath);

				for (PomXml.ArtifactNode subArtifactNode : curPomXml.depManages) {
					// dependencyManagement只做版本控制
					BuildGradle.MavenDependency cache = makeDep(subArtifactNode);

					if (subArtifactNode != cache) {
						//控制版本里的更新
						if (MavenDependencyVersion.compareTo(subArtifactNode.version, cache.version) > 0) {
							this.depMap.put(subArtifactNode.getGroupIdArtifactId(), subArtifactNode);
						}

					}
				}
				PomXml.ArtifactNode curArtifactNode = makeDep(new PomXml.ArtifactNode(curPomXml));
				Set<String> exclusionSet = curArtifactNode.getExclusionSet();

				for (PomXml.ArtifactNode subArtifactNode : curPomXml.deps) {
					if (exclusionSet.contains(subArtifactNode.getGroupIdArtifactId())) {
						continue;
					}
                    if (!vy(subArtifactNode)) {
                        yS(flatRepoPathMap, subArtifactNode, dependencyList, depth - 1);
                    }
                }
            }
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }












}

