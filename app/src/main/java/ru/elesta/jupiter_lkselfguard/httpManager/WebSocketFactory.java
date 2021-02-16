package ru.elesta.jupiter_lkselfguard.httpManager;

import ru.elesta.jupiter_lkselfguard.dataClasses.CommonServiceClass;
import ru.elesta.jupiter_lkselfguard.dataClasses.ContractClass;
import ru.elesta.jupiter_lkselfguard.dataClasses.ContractKROSClass;
import ru.elesta.jupiter_lkselfguard.dataClasses.DeviceClass;
import ru.elesta.jupiter_lkselfguard.dataClasses.DeviceOneClass;
import ru.elesta.jupiter_lkselfguard.dataClasses.EventClass;
import ru.elesta.jupiter_lkselfguard.dataClasses.ObjectClass;
import ru.elesta.jupiter_lkselfguard.dataClasses.ResponsibleClass;
import ru.elesta.jupiter_lkselfguard.dataClasses.SectionClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.WebSocket;
import ru.elesta.jupiter_lkselfguard.dataClasses.ServiceClass;
import ru.elesta.jupiter_lkselfguard.dataClasses.TrackClass;

public class WebSocketFactory {

    private ConcurrentHashMap<String, WebSocket> mapSockets = new ConcurrentHashMap<>();
    private WebSocket licenceSocket = null;
    private ArrayList<ObjectClass> setOfObjects = new ArrayList<>();
    private ArrayList<ObjectClass> commonSetOfObjects = new ArrayList<>();
    private ArrayList<SectionClass> setOfSections = new ArrayList<>();
    private ArrayList<EventClass> setOfEvents = new ArrayList<>();
    private ArrayList<EventClass> commonSetOfEvents = new ArrayList<>();
    private ArrayList<EventClass> setOfEventsOne = new ArrayList<>();
    private ArrayList<CommonServiceClass> listOfCommonServices = new ArrayList<>();
    private HashSet<DeviceClass> setOfDevices = new HashSet<>();
    private HashSet<DeviceClass> commonSetOfDevices = new HashSet<>();
    private HashSet<DeviceOneClass> setOfDevicesOne = new HashSet<>();
    private ContractClass licenseContract = null;
    private ContractKROSClass krosContract = null;
    private ArrayList<TrackClass> listOfCoordinates = new ArrayList<>();

    private HashSet<ResponsibleClass> setOfResponsibles = new HashSet<>();

     private static WebSocketFactory instance;

     static{
         instance = new WebSocketFactory();
    }

    public static WebSocketFactory getInstance(){
         return instance;
    }

    public WebSocket getLicenceSocket() {
        return licenceSocket;
    }

    public void setLicenceSocket(WebSocket licenceSocket) {
        this.licenceSocket = licenceSocket;
    }

    public ArrayList<ObjectClass> getSetOfObjects() {
        return setOfObjects;
    }

    public ConcurrentHashMap<String, WebSocket> getMapSockets() {
        return mapSockets;
    }

    public ArrayList<CommonServiceClass> getListOfCommonServices() {
        return listOfCommonServices;
    }

    public ArrayList<ObjectClass> getCommonSetOfObjects() {return commonSetOfObjects; }

    public ArrayList<SectionClass> getSetOfSections() {
        return setOfSections;
    }

    public HashSet<DeviceClass> getSetOfDevices() {
        return setOfDevices;
    }

    public HashSet<DeviceClass> getCommonSetOfDevices() {return commonSetOfDevices; }

    public HashSet<DeviceOneClass> getSetOfDevicesOne() {
        return setOfDevicesOne;
    }

    public ContractClass getLicenseContract() { return licenseContract; }

    public ContractKROSClass getKrosContract() { return krosContract; }

    public void setLicenseContract(ContractClass licenseContract) { this.licenseContract = licenseContract; }

    public void setKrosContract(ContractKROSClass krosContract) { this.krosContract = krosContract; }

    public HashSet<ResponsibleClass> getSetOfResponsibles() { return setOfResponsibles; }

    public ArrayList<EventClass> getSetOfEvents() {
        return setOfEvents;
    }

    public ArrayList<TrackClass> getListOfCoordinates() {
        return listOfCoordinates;
    }

    public ArrayList<EventClass> getCommonSetOfEvents() {
        return commonSetOfEvents;
    }

    public synchronized ArrayList<EventClass> getSetOfEventsOne() {
        return setOfEventsOne;
    }
}
