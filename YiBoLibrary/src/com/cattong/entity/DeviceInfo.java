package com.cattong.entity;

import com.cattong.commons.util.HashCodeHelper;

public class DeviceInfo extends BaseEntity {
	private static final long serialVersionUID = -127573237422958256L;

	private String deviceId;
	
	private String model;
	
	private Integer osNo;
	private Os os;
	
	private String fireware;
	
	private String cpu;
	
	private Long ram;
	
	private Long rom;
	

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Integer getOsNo() {
		return osNo;
	}

	public void setOsNo(Integer osNo) {
		this.osNo = osNo;
		this.os = Os.getOs(osNo);
	}

	public Os getOs() {
		return os;
	}

	public void setOs(Os os) {
		this.os = os;
		this.osNo = os == null ? Os.Unknow.getOsNo() : os.getOsNo();
	}

	public String getFireware() {
		return fireware;
	}

	public void setFireware(String fireware) {
		this.fireware = fireware;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public Long getRam() {
		return ram;
	}

	public void setRam(Long ram) {
		this.ram = ram;
	}

	public Long getRom() {
		return rom;
	}

	public void setRom(Long rom) {
		this.rom = rom;
	}

	@Override
	public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
			return false;
        }
        
        final DeviceInfo obj = (DeviceInfo) o;
        if (deviceId == null 
        	|| model == null) {
        	return false;
        }
        
        if (deviceId.equals(obj.getDeviceId()) 
        	&& model.equals(obj.getModel())) {
        	return true;
        }
        
        return false;
	}

	@Override
	public int hashCode() {
		HashCodeHelper helper = HashCodeHelper.getInstance();
		helper.appendObj(deviceId).appendObj(model);
		return helper.getHashCode();
	}

}
