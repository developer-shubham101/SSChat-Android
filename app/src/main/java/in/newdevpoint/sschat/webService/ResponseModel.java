package in.newdevpoint.sschat.webService;


public class ResponseModel<T> {

    private StatusModel status;

    private T data;

    public StatusModel getStatus() {
        return status;
    }

    public void setStatus(StatusModel status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}


