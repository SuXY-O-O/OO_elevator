package unused;

import source.PersonInfo;

public class RequestBuilder {
    public PersonInfo build(int type, int id, boolean action) {
        switch (type) {
            case 0 : {
                if (action) {
                    return new Request00(id);
                } else {
                    return new Request01(id);
                }
            }
            case 1 : {
                if (action) {
                    return new Request10(id);
                } else {
                    return new Request11(id);
                }
            }
            case 2 : {
                if (action) {
                    return new Request20(id);
                } else {
                    return new Request21(id);
                }
            }
            case 3 : {
                if (action) {
                    return new Request30(id);
                } else {
                    return new Request31(id);
                }
            }
            case 4 : {
                if (action) {
                    return new Request40(id);
                } else {
                    return new Request41(id);
                }
            }
            case 5 : {
                if (action) {
                    return new Request50(id);
                } else {
                    return new Request51(id);
                }
            }
            default: {
                return null;
            }
        }
    }
}
