package com.filetool.pojo;

/**
 * @Author: Jay
 * @Date: Created in 14:24 2018/3/13
 * @Modified By:
 */
public class Vm {

    /**
     * String:名称
     */
    private String vmName;

    /**
     * int:cpu规格
     */
    private int cpuReq;

    /**
     * int:mem规格需求
     */
    private int memReq;

    public Vm(String vmName, int cpuReq, int memReq) {
        this.vmName = vmName;
        this.cpuReq = cpuReq;
        this.memReq = memReq;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    public int getCpuReq() {
        return cpuReq;
    }

    public void setCpuReq(int cpuReq) {
        this.cpuReq = cpuReq;
    }

    public int getMemReq() {
        return memReq;
    }

    public void setMemReq(int memReq) {
        this.memReq = memReq;
    }
}
