package com.github.jchanghong.hutool.bloomfilter.filter;

import com.github.jchanghong.hutool.core.util.HashUtil;

public class RSFilter extends FuncFilter {
	private static final long serialVersionUID = 1L;

	public RSFilter(long maxValue) {
		this(maxValue, DEFAULT_MACHINE_NUM);
	}

	public RSFilter(long maxValue, int machineNum) {
		super(maxValue, machineNum, HashUtil::rsHash);
	}
}
