package io.github.zeroaicy.aide.extend;
import com.aide.codemodel.language.classfile.ClassFilePreProcessor;
import io.github.zeroaicy.aide.ClassReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * ZeroAicy实现的ClassFilePreProcessor
 */
public class ZeroAicyClassFilePreProcessor extends ClassFilePreProcessor {

	public ZeroAicyClassFilePreProcessor() {}

	private static ZeroAicyClassFilePreProcessor singleton;

	public static boolean isDefaultMethod(String methodSignature) {
		return ClassReader.hasDefaultMethod(methodSignature);
	}
	public static ClassFilePreProcessor getSingleton() {
		if( singleton == null ){
			singleton = new ZeroAicyClassFilePreProcessor();
		}
		return singleton;
	}

	@Override
	public Reader QX(String str, String str2, String str3) {
		Reader readClassFile = ClassReader.Dc_ReadClassFile(str, str2);
		if (readClassFile != null) {
			return readClassFile;
		}
		return super.QX(str, str2, str3);
	}
	@Override
	public List<String> J8(String zipFilePath, String listZipEntryName) {
		try {
			// str2 相对于Zip内部的路径
			Set<String> listZipNames = new HashSet<>();

			ZipFile zipFile = new ZipFile(zipFilePath);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				String zipEntryName = zipEntry.getName();

				if (zipEntryName.endsWith("/")) {
					//去除路径末尾 /
					zipEntryName = zipEntryName.substring(0, zipEntryName.length() - 1);
				}
				if (zipEntryName.equals(listZipEntryName)
					|| !zipEntryName.startsWith(listZipEntryName)) {
					continue;
				}

				if (listZipEntryName.length() > 0 && zipEntryName.charAt(listZipEntryName.length()) != '/') {
					//除了根目录 list ZipEntry子目录应该从/还是
					continue;
				}
				int indexOf = zipEntryName.indexOf('/', listZipEntryName.length() + 1);
				if (indexOf > 0) {
					listZipNames.add(zipFilePath + '/' + zipEntryName.substring(0, indexOf));
				}
				else {
					if (zipEntry.isDirectory()) {
						listZipNames.add(zipFilePath + '/' + zipEntryName);
					}
					else if ((zipEntryName.endsWith(".class")
							 && zipEntryName.indexOf('$') < 0) || zipEntryName.endsWith(".java")) {
						listZipNames.add(zipFilePath + '/' + zipEntryName);							
					}
					else if (zipEntryName.endsWith(".java")) {

						if (zipEntryName.startsWith("src/") 
							|| zipEntryName.startsWith("src\\")) {
							zipEntryName = zipEntryName.substring(4, zipEntryName.length());
						}
						else {
							listZipNames.add(zipFilePath + '/' + zipEntryName);
						}
					}
				}
			}
			zipFile.close();

			return Arrays.asList(listZipNames.toArray(new String[listZipNames.size()]));
		} catch (Throwable th) {
			throw new Error(th);
		}
	}


}
