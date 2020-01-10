package eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip;

public class CatchEvolutionSummaryListDTO extends CatchSummaryListDTO {

    @Override
    public void addSpecieAndQuantity(String speciesCode, Double weight, String areaName) {
        SpeciesQuantityDTO speciesQuantityDTO = new SpeciesQuantityDTO(speciesCode);

        if (getSpeciesList().contains(speciesQuantityDTO)) {
            int index = getSpeciesList().indexOf(speciesQuantityDTO);
            SpeciesQuantityDTO existingObject = getSpeciesList().get(index);
            existingObject.setWeight(existingObject.getWeight() + weight);
        } else {
            speciesQuantityDTO.setWeight(weight);
            getSpeciesList().add(speciesQuantityDTO);
        }

        this.setTotal(getTotal() + weight);
    }
}
