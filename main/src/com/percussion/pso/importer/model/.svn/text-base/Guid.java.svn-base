package com.percussion.pso.importer.model;

import javax.xml.bind.annotation.XmlAttribute;



public class Guid {

		private long guid;

		public Guid() {
			
		}
		public Guid(int id) {
			setId(id);
			setRevision(-1);
			setType(101);
		}
 		public Guid(Long guid) {
			this.guid = guid;
		}
		
		public long getGuid() {
			return guid;
		}

		public void setGuid(long guid) {
			this.guid = guid;
		}
		
		@XmlAttribute(name="uid")
		public int getId() {
			return getId(guid);
		}
		public void setId(int id) {
			long mask = 0xFFFFFFFF00000000l ;
			guid =  (mask & guid ) | Long.valueOf(id);
		}
		@XmlAttribute(name="revision")
		public int getRevision() {
			
			return getRevision(guid);
		}
		
		public void setRevision(int revision) {
			long mask = 0x000000FFFFFFFFFFl ;
			guid =  (mask & guid ) | Long.valueOf(revision) << 40;
		}
		@XmlAttribute(name="ObjectType")
		public int getType() {	
			return getType(guid);
		}
	
		public void setType(int type) {	
			long mask = 0xFFFFFF00FFFFFFFFl ;
			guid =  (mask & guid) | Long.valueOf(type) << 32;
		
		}
		
		public String toString() {
			return ""+getType() + "-" + getId() + "-" + getRevision();
		}
		
		public static int getId(Long id) {
			return (int)(id & 0xFFFFFFFFl);
		}
		public static  int getRevision(Long id) {
			
			return (int)(id >> 40);
		}
		public static int getType(Long id) {	
			return (int)(id >> 32) & 0xFF;
		}
}
