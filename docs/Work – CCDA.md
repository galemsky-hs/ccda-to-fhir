# Work – CCDA
created at 21.07.2022 22:36 >2022-07-21
#work/healthsamurai/ccda #tech/programming/parsing
*****

### CCDA broken down
* CCDA
	* CDA is built on RIM
	* CCDA – set of documents that can only contain specific CDA resources
	* Documents
		> Document is a set of constraints on CDA
		* Sections
			* Entries
	* Templates
		* Templates can be composed
		* T. can imply other templates (which inherit the current template)
			> US Realm Header can also be a Referral Note OR a Care Plan
			> Care plan is ALWAYS a US Realm header. CCD is ALWAYS a US Realm header
		* T. can contain other templates
	* XML
		* XML schemas
			* Can be used for validation
			* Can be used as a documentation
		- Validation schemas – can they be useful – only for validation (?)
		* Converted to json - just for convenience
	* Connection to FHIR
		* DIfferent representation for different IGs
		* **CDA must be mapped to FHIR without losses**
		* Any source of true for mapping
			- Source of true by template – ?
			> [CDA Mapping - Google Sheets](https://docs.google.com/spreadsheets/d/1KctdexG3oB2QBiBQNH1Rbt2uJ6DxQFROyIFKo5q95WU/edit%23gid=1223244219)
			> HL7, CDA on FHIR – [CDA-core-2.0/input/resources at master · HL7/CDA-core-2.0 · GitHub](https://github.com/HL7/CDA-core-2.0/tree/master/input/resources)
		- What is common and what is custom – ?
			- Omit these resources – ?
			- Default mapping (as is) ?
- Implementation
	- Client's ability to provide custom mappings
		* Mappings as data
	- Reusability
		- Mappings are bi-directional
			* Write code for both directions in one place

### Roadmap
- Validation (Optional)
	- XML – use a library with .xsd schemas
	- FHIR
		- External – use Zen IG schemas for validation
		- [-] From Aidbox – no validation
- [x] Parsing
	- [x] XML(bytes) → EDN/JSON
- Conversion
	- [-] Adapt input data for Microsoft FHIR .liquid templates
		* Drawbacks
			* One direction
			* Only windows
	- Custom Mappings
		- Choose/Implement mapping engine – 1
			- Isomer
			- x...
			- zenjute/jute 
			- Implement 3 
		- Rules – 4
			- Mapping rules CCDA→FHIR
				* Sources – 2
					* [HL7 – CDA Mapping - Google Sheets](https://docs.google.com/spreadsheets/d/1KctdexG3oB2QBiBQNH1Rbt2uJ6DxQFROyIFKo5q95WU/edit%23gid=1223244219)
					* HL7, CDA on FHIR – [CDA-core-2.0/input/resources at master · HL7/CDA-core-2.0 · GitHub](https://github.com/HL7/CDA-core-2.0/tree/master/input/resources)
					* [ccda-to-fhir/ on FHIR mapping language · GitHub](https://github.com/HL7/ccda-to-fhir/blob/master/mappings/ClinicalDocument.map)
					* Microsoft FHIR Converter
			- Mapping FHIR→CCDA
				- Inspired by CCDA→FHIR mappings
	- input/output streams
		- CCDA→FHIR → Bundle
		- FHIR→CCDA → XML

### POC
	- Example document – Referral Note
	- Make few rules for mapping
	- Implement basic mapping engine
	- Output – Patient FHIR resource

- Possible Outcomes
	- Requirements for mapping engine
	- What are the problems with 1=1 bi-directional approach

### Resources
[Oliver Egger - Map CDA to FHIR and Back with FHIR Mapping Language | DevDays June 2021 Virtual](https://www.youtube.com/watch?v=eoBXJJagVCU)
Template Viewer – https://trifolia.lantanagroup.com/TemplateManagement/View/II/2.16.840.1.113883.10.20.22.1.1/2015-08-01
