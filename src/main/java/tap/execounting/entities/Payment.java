package tap.execounting.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;

import tap.execounting.entities.interfaces.Dated;
import tap.execounting.util.DateUtil;

import static tap.execounting.util.DateUtil.format;

@Entity
@Table(name = "payments")
@NamedQueries({
		@NamedQuery(name = Payment.ALL, query = "Select p from Payment p"),
		@NamedQuery(name = Payment.BY_CONTRACT_ID, query = "Select p from Payment p where p.contractId = :contractId order by p.date desc"),
		@NamedQuery(name = Payment.BY_DATES, query = "Select p from Payment p where p.date between "
				+ ":earlierDate and :laterDate"),
		@NamedQuery(name = Payment.SCHEDULED, query = "from Payment p where p.scheduled = true"),
		@NamedQuery(name = Payment.NOT_SCHEDULED, query = "from Payment p where p.scheduled = false") })
public class Payment implements Dated {

	public static final String ALL = "Payment.all";

	public static final String BY_CONTRACT_ID = "Payment.byContractId";

	public static final String BY_DATES = "Payment.byDates";

	public static final String SCHEDULED = "Payment.scheduled";

	public static final String NOT_SCHEDULED = "Payment.notScheduled";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
    @NonVisual
	private int id;

	private int amount;

	@NonVisual
	private boolean deleted;

	private boolean scheduled;

	private String comment;

	@Validate(value = "required")
	private Date date;

	@Column(name = "contract_id")
    @NonVisual
	private int contractId;

	@ManyToOne
	@JoinColumn(name = "contract_id", updatable = false, insertable = false)
	private Contract contract;

	public Payment() {
		date = new Date();
	}

	public Contract getContract() {
		return contract;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getDate() {
		return date;
	}

    @Override
    public boolean isBetweenDates(Date one, Date two) {
        return !getDate().before(one) && getDate().before(two);
    }

    public void setDate(Date date) {
		this.date = date;
	}

	public int getContractId() {
		return contractId;
	}

	public void setContractId(int contractId) {
		this.contractId = contractId;
	}

	public boolean isScheduled() {
		return scheduled;
	}

	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

    public boolean isComplete() {
        return !isScheduled();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("От: ");
        sb.append(format("dd MMM YYYY", date));
        sb.append(", Сумма: ");
        sb.append(amount);
        if (scheduled)
            sb.append(" (план.)");
        if (comment != null)
            sb.append(" [" + comment + "]");

        return sb.toString();
    }
}
