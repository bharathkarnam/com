package com.medibox.auto.mapping.serviceImpl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.medibox.auto.mapping.dao.service.DistributorProductDAO;
import com.medibox.auto.mapping.dao.service.MasterProductDAO;
import com.medibox.auto.mapping.response.ResponseDTO;
import com.medibox.auto.mapping.service.AutoMappingService;
import com.medibox.auto.mapping.util.Constants;
import com.medibox.auto.mapping.util.SimpleExcelWriter;
import com.medibox.data.models.DistributorProduct;
import com.medibox.data.models.MasterProduct;
import com.savoirtech.logging.slf4j.json.LoggerFactory;
import com.savoirtech.logging.slf4j.json.logger.Logger;

@Service
public class AutoMappingServiceImpl implements AutoMappingService {

	@Autowired
	DistributorProductDAO distributorproduct;

	@Autowired
	MasterProductDAO masterproduct;

	Logger logger = LoggerFactory.getLogger(AutoMappingServiceImpl.class);

	private String distributorpackSize;

	private boolean distributorpacsizekFlag;

	private boolean distributorbrandFlag;

	private String distributorbrandname;

	private String distributormarketingCompany;

	private boolean distributormarketingcompanyFlag;

	private String distributorProductName;

	private boolean distributorprodnameFlag;

	int rowCount = 0; // row count for excel sheet

	private String token[];

	@Value("${mongoWrite}")
	private boolean mongoWrite;

	@Async
	@Override
	public ResponseDTO mapAllunMappedDistributors() {
		// this function will map all the unmapped distributor product to master list
		try {
			// System.out.println("mongo write" + mongoWrite);
			// get all unmapped Distributor
			List<DistributorProduct> distproductslist = distributorproduct.getUnmappedDistributorProducts(new ObjectId("59e08d9a97f3ae335f00006f"));

			SimpleExcelWriter excelWriter = new SimpleExcelWriter(); // excel writer helps us in writting to excel
			int count = 0;
			for (DistributorProduct singledistributorentity : distproductslist) {
				++count;
//				 if(count == 500) {
//				 break;
//				 }
				System.out.println("count"+count);
				try {
					setvariable(singledistributorentity); // set important variables i.e. name, marketing company, brand
															// and
															// packsize

					if (null != distributorbrandname || !distributorbrandname.isEmpty()) {
						token = distributorProductName.split(" ");
						String namestrength = isproductInNameStrengthPattern(token); // check for name in name strength
																						// pattern
						// System.out.println("strength" + namestrength);
						String distributorsplitproductname = token[0].replaceAll("[-+.^:,]", " ").replace(">", "");

						if (distributorprodnameFlag == true && distributorbrandFlag == true
								&& distributorpacsizekFlag == true && distributormarketingcompanyFlag == true) {
							List<MasterProduct> masterproductlist = masterproduct
									.getMasterProductsProductsByNameBrandPckSzandMC(
											distributorsplitproductname + Constants.REGEX_CONSTANT + namestrength,
											distributorbrandname, distributorpackSize, distributormarketingCompany);

							saveToMongoOrExcel(singledistributorentity, masterproductlist, excelWriter);

							System.out.println(masterproductlist.size() + " dist1 " + distributorsplitproductname
									+ Constants.DIST_BRAND + distributorbrandname + " packSize: " + distributorpackSize
									+ " mc: " + distributormarketingCompany);
						} else if (distributorbrandFlag == false && distributormarketingcompanyFlag == true
								&& distributorpacsizekFlag == true) {
							List<MasterProduct> masterproductlist = masterproduct.getMasterProductsByNamePckSzandMC(
									distributorsplitproductname + Constants.REGEX_CONSTANT + namestrength,
									distributorpackSize, distributormarketingCompany);

							saveToMongoOrExcel(singledistributorentity, masterproductlist, excelWriter);

							System.out.println(masterproductlist.size() + " dist2 " + distributorsplitproductname
									+ " namestrength " + namestrength + " packsize" + distributorpackSize + " mc: "
									+ distributormarketingCompany);
						} else if (distributorbrandFlag == true && distributormarketingcompanyFlag == true
								&& distributorpacsizekFlag == false) {
							List<MasterProduct> masterproductlist = masterproduct
									.getMasterProductsProductsByNamebrandandMC(
											distributorsplitproductname + Constants.REGEX_CONSTANT + namestrength,
											distributorbrandname, distributormarketingCompany);
							System.out.println(masterproductlist.size() + " dist3 " + distributorsplitproductname);
							saveToMongoOrExcel(singledistributorentity, masterproductlist, excelWriter);

						} else if (distributorbrandFlag == true && distributormarketingcompanyFlag == false
								&& distributorpacsizekFlag == true) {
							List<MasterProduct> masterproductlist = masterproduct
									.getMasterProductsProductsByNamebrandandPckSZ(
											distributorsplitproductname + Constants.REGEX_CONSTANT + namestrength,
											distributorbrandname, distributorpackSize);

							saveToMongoOrExcel(singledistributorentity, masterproductlist, excelWriter);

							//System.out.println(masterproductlist.size() + " dist4 " + distributorsplitproductname);

						}

					}
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.trace().message(e.getMessage()).field("eventName", "MappingServiceError").field("ERROR CODE", "5001")
					.log();
			;
		}
		return null;
	}

	private void saveToMongoOrExcel(DistributorProduct singledistributorentity, List<MasterProduct> masterproductlist,
			SimpleExcelWriter excelwriter) {

		if (masterproductlist.size() == 1) {
			++rowCount;
			if (mongoWrite == true) {
				singledistributorentity.setMasterProductId(masterproductlist.get(0).getId());
				// distributorproduct.save(singledistributorentity);
			} else {
				writetoExcel(singledistributorentity, masterproductlist.get(0), excelwriter);
			}

		//	 System.out.println(rowCount);
		}
	}

	private void writetoExcel(DistributorProduct singledistributorentity, MasterProduct masterProduct,
			SimpleExcelWriter excelwriter) {

		excelwriter.writeToExcel(Constants.DIST_NAME + singledistributorentity.getName() + Constants.DIST_PACKSIZE
				+ singledistributorentity.getPackSize() + Constants.DIST_BRAND + singledistributorentity.getBrand()
				+ Constants.DIST_MARKETINGCOMAPANY + singledistributorentity.getMarketingCompany().getName(),
				Constants.MP_NAME + masterProduct.getName() + Constants.MP_PACKSIZE + masterProduct.getPackSize()
						+ Constants.MP_BRAND + masterProduct.getBrand() + Constants.MP_MARKETINGCOMAPANY
						+ masterProduct.getMarketingCompany().getName(),
				rowCount);

	}

	private String isproductInNameStrengthPattern(String[] token) {
		try {
			int i = 0;
			// System.out.println(token[1]);
			String regex = "(\\d+)"; // Integer Number

			Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			for (i = 1; i < token.length; i++) {
				Matcher m = p.matcher(token[i]);
				if (m.find()) {
					String mappedstrength = m.group(1);
					if (mappedstrength.length() >= 2) {
						// System.out.println(mappedstrength+" strength: "+"\n");
						return mappedstrength;
					}
				}
			}
		} catch (Exception e) {

		}
		return "";
	}

	private void setvariable(DistributorProduct singledistributorentity) {
		if (null != singledistributorentity.getName() && !singledistributorentity.getName().trim().isEmpty()) {
			distributorProductName = singledistributorentity.getName();
			distributorprodnameFlag = true;
		} else {
			distributorprodnameFlag = false;
		}
		if (null != singledistributorentity.getPackSize() && !singledistributorentity.getPackSize().trim().isEmpty()) {
			distributorpackSize = singledistributorentity.getPackSize().replaceAll("\\W", Constants.REGEX_CONSTANT)
					.trim();
			distributorpacsizekFlag = true;
		} else {
			distributorpacsizekFlag = false;
		}
		if (null != singledistributorentity.getBrand() && !singledistributorentity.getBrand().trim().isEmpty()) {
			distributorbrandname = singledistributorentity.getBrand();
			distributorbrandFlag = true;
		} else {
			distributorbrandFlag = false;
		}
		if (null != singledistributorentity.getMarketingCompany().getName()
				&& !singledistributorentity.getMarketingCompany().getName().trim().isEmpty()) {
			try {
				String[] companysplitstring = singledistributorentity.getMarketingCompany().getName().split(" ");
				String[] comapnynamesubstringsplit = companysplitstring[0].split("-");
				distributormarketingCompany = comapnynamesubstringsplit[0] + Constants.REGEX_CONSTANT;
				if (distributormarketingCompany.contains("mc")) {
					distributormarketingCompany = distributormarketingCompany.replaceAll("mc", "").trim();
				}
				distributormarketingcompanyFlag = true;
			} catch (Exception e) {
				distributormarketingcompanyFlag = false;
			}
		} else {
			distributormarketingcompanyFlag = false;
		}
	}

	@SuppressWarnings("null")
	@Override
	public long getCountDistributor() {
		try {
			return distributorproduct.getcount();
		} catch (Exception e) {
			logger.trace().message(e.getMessage()).field("eventName", "MappingServiceError").field("ERROR CODE", "5001")
					.log();

		}
		return (Long) null;
	}

	@Override
	public List<MasterProduct> getAllMasterProducts() {

		return null;
	}

}
