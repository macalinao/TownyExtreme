package com.simplyian.towny.towny.question.towny;

import com.simplyian.towny.towny.Towny;
import com.simplyian.towny.towny.object.TownyUniverse;
import com.simplyian.towny.towny.question.QuestionTask;

public abstract class TownyQuestionTask extends QuestionTask {
    protected Towny towny;

    protected TownyUniverse universe;

    public TownyUniverse getUniverse() {
        return universe;
    }

    public void setTowny(Towny towny) {
        this.towny = towny;
        this.universe = towny.getTownyUniverse();
    }

    @Override
    public abstract void run();

}
