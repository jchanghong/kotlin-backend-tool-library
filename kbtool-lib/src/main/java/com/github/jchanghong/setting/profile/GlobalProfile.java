package com.github.jchanghong.setting.profile;

import com.github.jchanghong.core.lang.Singleton;
import com.github.jchanghong.setting.Setting;

/**
 * 全局的Profile配置中心
 * 
 * @author Looly
 *
 */
public class GlobalProfile {

	private GlobalProfile() {
	}

	// -------------------------------------------------------------------------------- Static method start
	/**
	 * 设置全局环境
	 * @param profile 环境
	 * @return {@link Profile}
	 */
	public static Profile setProfile(String profile) {
		return Singleton.get(Profile.class, profile);
	}

	/**
	 * 获得全局的当前环境下对应的配置文件
	 * @param settingName 配置文件名，可以忽略默认后者（.setting）
	 * @return {@link Setting}
	 */
	public static Setting getSetting(String settingName) {
		return Singleton.get(Profile.class).getSetting(settingName);
	}
	// -------------------------------------------------------------------------------- Static method end
}
