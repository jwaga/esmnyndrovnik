package pl.eastsidemandala.nyndrovnik;

/**
 * 'Business' data on practices.
 * Created by jwaga on 03.06.2016.
 */
public enum Practice {

    SHORT_REFUGE(R.string.short_refuge_short, R.string.short_refuge, R.drawable.refuge, 11000),
    PROSTRATIONS(R.string.prostrations_short, R.string.prostrations, R.drawable.prostr, 111111),
    DIAMOND_MIND(R.string.diamond_mind_short, R.string.diamond_mind, R.drawable.dm, 111111),
    MANDALA_OFFERING(R.string.mandala_offering_short, R.string.mandala_offering, R.drawable.mandala, 111111),
    GURU_YOGA(R.string.guru_yoga_short, R.string.guru_yoga, R.drawable.guru, 111111),
    AMITABHA(R.string.amitabha_short, R.string.amitabha, R.drawable.phowa, 500000),
    LOVING_EYES(R.string.lovingeyes_short, R.string.lovingeyes, R.drawable.lovingeyes, 1000000);

    int name_res;
    int text_res;
    int image_res;
    int repetitions_max;

    Practice(int name_res, int text_res, int image_res, int repetitions_max) {
        this.name_res = name_res;
        this.text_res = text_res;
        this.image_res = image_res;
        this.repetitions_max = repetitions_max;
    }

    public int getNameRes() { return this.name_res; }

    public int getTextRes() {return text_res; }

    public int getImageRes() {
        return this.image_res;
    }

    public int getRepetitionsMax() {
        return this.repetitions_max;
    }
}
