package io.github.swagree.pokeextra.handler;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;

import java.util.List;

public class ListProcessingResult {
    private boolean listFlag;
    private List<String> flagCommands;
    private Pokemon pokemon;

    public ListProcessingResult(boolean listFlag, List<String> flagCommands, Pokemon pokemon) {
        this.listFlag = listFlag;
        this.flagCommands = flagCommands;
        this.pokemon = pokemon;
    }

    public boolean isListFlag() {
        return listFlag;
    }

    public List<String> getFlagCommands() {
        return flagCommands;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }
}
